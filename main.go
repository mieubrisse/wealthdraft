package main

import (
	"flag"
	"fmt"
	"github.com/mieubrisse/financial-predictor/progressive_tax_calculator"
	"github.com/mieubrisse/financial-predictor/scenario_parser"
	"github.com/mieubrisse/financial-predictor/tax_bracket_parser"
	"github.com/sirupsen/logrus"
	"os"
)

const (
	failureExitCode = 1
)

func main() {
	// Parse flags
	scenariosFilepathArg := flag.String(
		"scenarios",
		"",
		"YAML file containing scenarios to calculate this year's tax for")
	fedIncomeTaxBracketsFilepathArg := flag.String(
		"federal-income-tax-brackets",
		"",
		"CSV file of federal income tax brackets")
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

	brackets, err := tax_bracket_parser.GetLatestBrackets(*fedIncomeTaxBracketsFilepathArg)
	exitWithErrorIfExists(err, "An error occurred parsing the federal income tax brackets file")

	for idx, scenario := range(scenarios) {
		logrus.Infof("================ Scenario %v ===============", idx)
		calculateScenario(scenario, brackets)
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

func calculateScenario(scenario scenario_parser.Scenario, fedIncomeTaxBrackets []tax_bracket_parser.TaxBracket) {
	var totalEarnedIncome float64 = 0
	for _, earnedIncome := range(scenario.EarnedIncome) {
		totalEarnedIncome += earnedIncome.Amount
	}
	logrus.Info("Total Earned Income: %v", totalEarnedIncome)

	federalIncomeTax := progressive_tax_calculator.Calculate(fedIncomeTaxBrackets, totalEarnedIncome)
	logrus.Info("Federal Income Tax: %v", federalIncomeTax)


}