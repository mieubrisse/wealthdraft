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
	for year, constants := range(allGovConstants) {
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

func calculateScenario(scenario scenario_parser.Scenario, govConstants gov_constant_parser.GovConstantsForYear) error {
	if err := scenario_validator.ValidateScenario(scenario, govConstants); err != nil {
		return stacktrace.Propagate(err, "An error occurred during scenario validation")
	}

	var earnedIncome float64 = 0
	for _, earnedIncomePart := range(scenario.EarnedIncome) {
		earnedIncome += earnedIncomePart.Amount
	}
	logrus.Info("Earned Income: %v", earnedIncome)

	// IRA contributions MUST be done with earned income!!
	// See: https://www.investopedia.com/retirement/ira-contribution-limits/
	regIraContrib := scenario.ContribIRA.Regular * govConstants.Retirement.IraContribLimit
	rothIraContrib := scenario.ContribIRA.Roth * govConstants.Retirement.IraContribLimit
	totalIraContrib := regIraContrib + rothIraContrib
	if earnedIncome < totalIraContrib {
		return stacktrace.NewError(
			"IRA contributions are limited to earned income, but earned income %v is < total IRA contrib %v",
			earnedIncome,
			totalIraContrib)
	}

	// TODO add interest, dividends, STCG, and LTCG
	var totalUnearnedIncome float64 = 0
	logrus.Info("Unearned Income: %v", totalUnearnedIncome)

	logrus.Info(sumLine)
	grossIncome := earnedIncome + totalUnearnedIncome
	logrus.Info("Gross Income: %v", grossIncome)

	// regular 401k contribs don't show up at all in "wages, salaries, and tips"
	reg401kContrib := scenario.Contrib401k.Regular * govConstants.Retirement.Personal401kContribLimit
	logrus.Info("Trad 401k Deduction: -%v", reg401kContrib)

	logrus.Info(sumLine)
	grossIncomeLess401k := grossIncome - reg401kContrib
	logrus.Info("IRS Gross Income: %v (less 401k deductions)", grossIncomeLess401k)

	// TODO add box 2, tax-exempt interest
	// TODO add box 3, qualified & ordinary dividends
	// TODO add box 4, IRA distributions (it was in my 2019 tax return)

	// TODO incorporate IRA deductible limit (if AGI is < 65k)


	// TODO implement HSA deduction here

	// MAGI (which DOESN'T show up on the 1040) is sometimes defined as "AGI, with certain deductions like
	// IRA contrib added back in". If we sue this way, we get a circular dependency: you need IRA contrib to calculate
	//  AGI to calculate MAGI to determine whether you can include IRA contributions:
	//  https://money.stackexchange.com/questions/94585/circular-dependency-involving-ira-deduction
	// Instead, we do things in the logical order and find MAGI *first*
	// See also : https://www.investopedia.com/terms/m/magi.asp
	// TODO Implement HSA deduction here, AND with AGI calculation
	logrus.Info(sumLine)
	modifiedAdjustedGrossIncome := grossIncomeLess401k
	logrus.Info("MAGI: %v", modifiedAdjustedGrossIncome)





	// NOTE NOTE NOTE: all the below is for AGI calculation, which I'm NOT sure is that useful

	var adjustmentsForAGI float64 = 0
	excludedFEI := math.Min(
		govConstants.Foreign.ForeignEarnedIncomeExemption,
		scenario.FractionForeignEarnedIncome * earnedIncome)
	if excludedFEI > 0 {
		logrus.Info("FEI Exclusion: -%v", excludedFEI)
	}
	adjustmentsForAGI -= excludedFEI

	// Figure out how much the trad IRA deduction reduces by (if anything)
	phaseoutRangeFill := math.Max(0, modifiedAdjustedGrossIncome - govConstants.Retirement.TradIraDeductiblePhaseoutFloor)
	phaseoutRangeWidth := govConstants.Retirement.TradIraDeductiblePhaseoutCeiling - govConstants.Retirement.TradIraDeductiblePhaseoutFloor
	phaseoutRangeFillPct := math.Min(1, phaseoutRangeFill / phaseoutRangeWidth)
	deductionMultiplier := 1 / phaseoutRangeFillPct
	tradIraDeduction := deductionMultiplier * regIraContrib
	if tradIraDeduction > 0 {
		logrus.Info("Trad IRA Deduction: -%v", tradIraDeduction)
	}
	adjustmentsForAGI -= tradIraDeduction

	// TODO add interest deductions, and other things that only AGI (and not MAGI) receives

	logrus.Info(sumLine)
	adjustedGrossIncome := modifiedAdjustedGrossIncome + adjustmentsForAGI
	logrus.Info("AGI: %v", adjustedGrossIncome)

	var adjustmentsForTaxableIncome float64 = 0

	logrus.Info("Standard Deduction: -%v", govConstants.StandardDeduction)
	adjustmentsForTaxableIncome -= govConstants.StandardDeduction

	logrus.Info(sumLine)
	taxableIncome := adjustedGrossIncome + adjustmentsForTaxableIncome
	logrus.Info("Taxable Income: %v", taxableIncome)






	// TODO reduce by deductions, ordinary income first
	// Deductions apply to ordinary income first, THEN capital gains
	// See: https://www.kitces.com/blog/long-term-capital-gains-bump-zone-higher-marginal-tax-rate-phase-in-0-rate




	foreignEarnedIncome -


	excludedForeignEarnedIncome

	federalIncomeTax := progressive_tax_calculator.Calculate(govConstants.FederalIncomeBrackets, earnedIncome)
	logrus.Info("Federal Income Tax: %v", federalIncomeTax)
}
