package main

import (
	"flag"
	"fmt"
	"github.com/mieubrisse/financial-predictor/progressive_tax_calculator"
	"github.com/mieubrisse/financial-predictor/tax_bracket_parser"
	"github.com/sirupsen/logrus"
	"os"
)

const (
	failureExitCode = 1
)

func main() {
	fedIncomeTaxBracketsArg := flag.String(
		"federal-income-tax-brackets",
		"",
		"CSV file of federal income tax brackets")
	logLevelArg := flag.String(
		"log-level",
		"info",
		"Log level to output at (trace, debug, info, warn, error, fatal")
	flag.Parse()

	logLevelStr := *logLevelArg
	logLevel, err := logrus.ParseLevel(logLevelStr)
	if err != nil {
		fmt.Errorf("An error occurred parsing log level string '%v'\n", logLevelStr)
		os.Exit(failureExitCode)
	}
	logrus.SetLevel(logLevel)

	brackets, err := tax_bracket_parser.GetLatestBrackets(*fedIncomeTaxBracketsArg)
	exitWithErrorIfExists(err, "An error occurred parsing the federal income tax brackets file")

	tax := progressive_tax_calculator.Calculate(brackets, 250000)
	logrus.Infof("Tax: %v", tax)
}

func exitWithErrorIfExists(err error, humanReadableText string) {
	if err == nil {
		return
	}

	logrus.Error(humanReadableText)
	fmt.Fprintln(logrus.StandardLogger().Out, err)
	os.Exit(failureExitCode)
}