package main

import (
	"flag"
	"fmt"
	"github.com/mieubrisse/financial-predictor/gov_constant_parser"
	"github.com/mieubrisse/financial-predictor/progressive_tax_calculator"
	"github.com/mieubrisse/financial-predictor/scenario_parser"
	"github.com/mieubrisse/financial-predictor/scenario_validator"
	"github.com/palantir/stacktrace"
	"github.com/sirupsen/logrus"
	"math"
	"os"
	"time"
)

const (
	failureExitCode = 1
	sumLine = "----------------------------------------------"
)

type allIncome struct {
	earnedIncome float64
	otherUnearnedIncome float64
	stcg float64
	ltcg float64
}

type allTaxes struct {
	federalIncome float64
	socialSecurity float64
	medicare float64
	niit float64
	stcg float64
	ltcg float64
}

func main() {
	// Parse flags
	scenariosFilepathArg := flag.String(
		"scenarios",
		"",
		"YAML file containing scenarios to calculate this year's tax for")
	govConstantsFilepathArg := flag.String(
		"gov-constants",
		"",
		"YAML file of gov constants per year")
	logLevelArg := flag.String(
		"log-level",
		"info",
		"Log level to output at (trace, debug, info, warn, error, fatal")
	flag.Parse()

	// Set log level
	logLevelStr := *logLevelArg
	logLevel, err := logrus.ParseLevel(logLevelStr)
	if err != nil {
		fmt.Errorf("An error occurred parsing log level string '%v'\n", logLevelStr)
		os.Exit(failureExitCode)
	}
	logrus.SetLevel(logLevel)

	scenarios, err := scenario_parser.GetScenarios(*scenariosFilepathArg)
	exitWithErrorIfExists(err, "An error occurred parsing the scenarios file")

	allGovConstants, err := gov_constant_parser.GetGovConstants(*govConstantsFilepathArg)
	exitWithErrorIfExists(err, "An error occurred parsing the gov constants file")

	latestYear := 0
	for year, _ := range(allGovConstants) {
		if year > latestYear {
			latestYear = year
		}
	}
	latestConstants := allGovConstants[latestYear]

	if time.Now().Year() != latestYear {
		logrus.Warn("The latest gov constants we have are old, from %v!!!", latestYear)
	}

	for idx, scenario := range(scenarios) {
		logrus.Infof("================ Scenario %v ===============", idx)
		logrus.Debugf("Scenario: %+v", scenario)
		calculateScenario(scenario, latestConstants)
	}
}

func exitWithErrorIfExists(err error, humanReadableText string) {
	if err == nil {
		return
	}

	logrus.Error(humanReadableText)
	fmt.Fprintln(logrus.StandardLogger().Out, err)
	os.Exit(failureExitCode)
}

/*
NOTE: This does NOT calculate in the same was as the 1040, which is very confusing (lots of subtract and adding back,
	etc. Instead, this tries to provide a more straightforward approach that gets to the same results
 */
func calculateScenario(scenario scenario_parser.Scenario, govConstants gov_constant_parser.GovConstantsForYear) error {
	if err := scenario_validator.ValidateScenario(scenario, govConstants); err != nil {
		return stacktrace.Propagate(err, "An error occurred during scenario validation")
	}

	grossIncomeStreams := sumIncomeStreams(scenario)

	// IRA contributions MUST be done with earned income!!
	// See: https://www.investopedia.com/retirement/ira-contribution-limits/
	regIraContrib := scenario.ContribIRA.Regular * govConstants.Retirement.IraContribLimit
	rothIraContrib := scenario.ContribIRA.Roth * govConstants.Retirement.IraContribLimit
	totalIraContrib := regIraContrib + rothIraContrib
	if grossIncomeStreams.earnedIncome < totalIraContrib {
		return stacktrace.NewError(
			"IRA contributions are limited to earned income, but earned income %v is < total IRA contrib %v",
			grossIncomeStreams.earnedIncome,
			totalIraContrib)
	}

	logrus.Infof("Earned Income: %v", grossIncomeStreams.earnedIncome)
	logrus.Infof("Longterm Cap Gains: %v", grossIncomeStreams.ltcg)
	logrus.Infof("Shortterm Cap Gains: %v", grossIncomeStreams.stcg)
	logrus.Infof("Other Unearned Income: %v", grossIncomeStreams.otherUnearnedIncome)
	logrus.Info(sumLine)
	grossIncome := grossIncomeStreams.earnedIncome + grossIncomeStreams.ltcg + grossIncomeStreams.stcg + grossIncomeStreams.otherUnearnedIncome
	logrus.Infof("Gross Income: %v", grossIncome)

	reg401kContrib := scenario.Contrib401k.Regular * govConstants.Retirement.Personal401kContribLimit

	// MAGI (which DOESN'T show up on the 1040) is sometimes defined as "AGI, with certain deductions like
	// IRA contrib added back in". If we sue this way, we get a circular dependency: you need IRA contrib to calculate
	//  AGI to calculate MAGI to determine whether you can include IRA contributions:
	//  https://money.stackexchange.com/questions/94585/circular-dependency-involving-ira-deduction
	// Instead, we do things in the logical order and find MAGI *first*
	// See also : https://www.investopedia.com/terms/m/magi.asp
	modifiedAdjustedGrossIncome := grossIncome - reg401kContrib // TODO implement HSA deduction here

	// Figure out how much the trad IRA deduction reduces by (if anything)
	phaseoutRangeFill := math.Max(0, modifiedAdjustedGrossIncome - govConstants.Retirement.TradIraDeductiblePhaseoutFloor)
	phaseoutRangeWidth := govConstants.Retirement.TradIraDeductiblePhaseoutCeiling - govConstants.Retirement.TradIraDeductiblePhaseoutFloor
	phaseoutRangeFillPct := math.Min(1, phaseoutRangeFill / phaseoutRangeWidth)
	deductionMultiplier := 1 / phaseoutRangeFillPct
	tradIraDeduction := deductionMultiplier * regIraContrib

	totalDeductions := reg401kContrib + tradIraDeduction + govConstants.StandardDeduction
	taxableIncomeStreams := applyDeductions(grossIncomeStreams, totalDeductions)
	taxes := calculateTaxes(taxableIncomeStreams, scenario.FractionForeignEarnedIncome, govConstants)

	logrus.Info("")
	logrus.Infof("Fed Income Tax: %v", taxes.federalIncome)
	logrus.Infof("Social Security Tax: %v", taxes.socialSecurity)
	logrus.Infof("Medicare Tax: %v", taxes.medicare)
	logrus.Infof("NIIT: %v", taxes.niit)
	logrus.Infof("STCG Tax: %v", taxes.stcg)
	logrus.Infof("LTCG Tax: %v", taxes.ltcg)
	logrus.Info(sumLine)
	totalTax := taxes.federalIncome + taxes.socialSecurity + taxes.medicare + taxes.niit + taxes.stcg + taxes.ltcg
	logrus.Infof("Total Tax: %v", totalTax)
	marginalTaxRate := totalTax / grossIncome
	logrus.Infof("Marginal Tax Rate: %v", marginalTaxRate)

	return nil
}

func sumIncomeStreams(scenario scenario_parser.Scenario) allIncome {
	var earnedIncome float64 = 0
	for _, earnedIncomePart := range (scenario.EarnedIncome) {
		earnedIncome += earnedIncomePart
	}

	var ltcg float64 = 0
	for _, ltcgPart := range scenario.LongTermCapitalGains {
		ltcg += ltcgPart
	}

	var stcg float64 = 0
	for _, stcgPart := range scenario.ShortTermCapitalGains {
		stcg += stcgPart
	}

	// TODO add interest & qualified/regular dividends here
	var otherUnearnedIncome float64 = 0

	return allIncome{
		earnedIncome:        earnedIncome,
		otherUnearnedIncome: otherUnearnedIncome,
		stcg:                stcg,
		ltcg:                ltcg,
	}
}

/*
Applies the deductions in the proper order to the given incomes

Returns: The input income streams with deductions applied
 */
func applyDeductions(
		income allIncome,
		deductions float64) allIncome {
	// Deductions get applied to earned income first, and only after to unearned income (which is good)
	// See: https://www.kitces.com/blog/long-term-capital-gains-bump-zone-higher-marginal-tax-rate-phase-in-0-rate
	incomesToReduceInOrder := []float64{
		income.earnedIncome,
		income.otherUnearnedIncome,
		income.stcg,
		income.ltcg,
	}
	resultingReducedIncomes := []float64{}
	remainingDeduction := deductions
	for _, incomeToReduce := range incomesToReduceInOrder {
		resultingIncome := incomeToReduce
		if remainingDeduction > 0 {
			amountToReduce := math.Min(remainingDeduction, incomeToReduce)
			resultingIncome = incomeToReduce - amountToReduce
			remainingDeduction -= amountToReduce
		}
		resultingReducedIncomes = append(resultingReducedIncomes, resultingIncome)
	}
	
	return allIncome{
		earnedIncome:        resultingReducedIncomes[0],
		otherUnearnedIncome: resultingReducedIncomes[1],
		stcg:                resultingReducedIncomes[2],
		ltcg:                resultingReducedIncomes[3],
	}
}

/*
Calculates the big taxes on the input. Note that the args should be POST-DEDUCTION income!
 */
func calculateTaxes(income allIncome, fractionForeignEarnedIncome float64, govConstants gov_constant_parser.GovConstantsForYear) allTaxes {
	taxableEarnedIncome := income.earnedIncome
	taxableOtherUnearnedIncome := income.otherUnearnedIncome
	taxableStcg := income.stcg
	taxableLtcg := income.ltcg

	// Federal income tax
	fedIncomeTaxableIncome := taxableEarnedIncome + taxableOtherUnearnedIncome + taxableStcg
	fedIncomeTaxBeforeFEIE := progressive_tax_calculator.Calculate(govConstants.FederalIncomeBrackets, fedIncomeTaxableIncome)
	excludedFEI := math.Min(
		govConstants.Foreign.ForeignEarnedIncomeExemption,
		fractionForeignEarnedIncome * taxableEarnedIncome)
	fedIncomeTax := fedIncomeTaxBeforeFEIE - progressive_tax_calculator.Calculate(govConstants.FederalIncomeBrackets, excludedFEI)

	// Social security tax
	socialSecurityTaxableAmount := math.Min(govConstants.FICA.SocialSecurityWageCap, taxableEarnedIncome)
	socialSecurityTax := govConstants.FICA.SocialSecurityRate * socialSecurityTaxableAmount

	// Medicare tax
	medicareTax := govConstants.FICA.MedicareBaseRate * taxableEarnedIncome
	medicareSurtaxableAmount := math.Max(0, taxableEarnedIncome - govConstants.FICA.MedicareSurtaxFloor)
	medicareTax += medicareSurtaxableAmount * govConstants.FICA.MedicareSurtaxExtraRate

	// Net Investment Income Tax (aka Unearned Income Medicare Contribution Surtax)
	investmentIncome := taxableStcg + taxableLtcg + taxableOtherUnearnedIncome
	niitTaxableAmount := math.Max(0, investmentIncome - govConstants.FICA.NetInvestmentIncomeThreshold)
	niitTax := niitTaxableAmount * govConstants.FICA.NetInvestmentIncomeTaxRate

	// Capital gains
	stcgTax := progressive_tax_calculator.Calculate(govConstants.FederalIncomeBrackets, taxableStcg)
	ltcgTax := progressive_tax_calculator.Calculate(govConstants.FederalLTCGBrackets, taxableLtcg)

	return allTaxes{
		federalIncome:  fedIncomeTax,
		socialSecurity: socialSecurityTax,
		medicare:       medicareTax,
		niit:           niitTax,
		stcg:           stcgTax,
		ltcg:           ltcgTax,
	}
}

