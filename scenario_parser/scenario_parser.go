package scenario_parser

import (
	"github.com/go-yaml/yaml"
	"github.com/palantir/stacktrace"
	"io/ioutil"
)

// Struct representing classification of IRA or 401k contributions
// The percentages are percentages OF THE YEAR'S TOTAL LIMIT, and should add up to 1.0!
type ContributionBalance struct {
	Regular float64	`yaml:"regular"`
	Roth float64	`yaml:"roth"`
}

// TODO Switch all the values in this file to use something other than float64, which has small precision problems:
//  See: https://github.com/shopspring/decimal
type Scenario struct {
	Contrib401k ContributionBalance `yaml:"contrib401k""`

	ContribIRA ContributionBalance	`yaml:"contribIRA"`

	EarnedIncome []float64	`yaml:"earnedIncome"`

	// What fraction of my earned income was from foreign sources
	FractionForeignEarnedIncome float64	`yaml:"fractionForeignEarnedIncome"`

	LongTermCapitalGains []float64	`yaml:"longTermCapitalGains"`

	ShortTermCapitalGains []float64	`yaml:"shortTermCapitalGains"`
}

/*
Parses a YAML file containing scenario information into scenario objects which can be used when making projections
 */
func GetScenarios(scenariosFilepath string) (map[string]Scenario, error) {
	fileBytes, err := ioutil.ReadFile(scenariosFilepath)
	if err != nil {
		return nil, stacktrace.Propagate(err, "An IO error occurred reading scenarios file '%v'", scenariosFilepath)
	}

	var result map[string]Scenario
	if err := yaml.UnmarshalStrict(fileBytes, &result); err != nil {
		return nil, stacktrace.Propagate(err, "An error occurred deserializing the scenarios file contents")
	}
	return result, nil
}