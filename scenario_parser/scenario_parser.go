package scenario_parser

import (
	"github.com/go-yaml/yaml"
	"github.com/palantir/stacktrace"
	"io/ioutil"
)

type AnnotatedAmount struct {
	Amount float64
	Note string
}

// Struct representing classification of IRA or 401k contributions
// The percentages are percentages OF THE YEAR'S TOTAL LIMIT
type RegOrRothPct struct {
	RegularPct float64
	RothPct float64
}

type Scenario struct {
	EarnedIncome []AnnotatedAmount

	// What percentage of my earned income was from foreign sources
	ForeignEarnedIncomePct float64

	Contrib401k RegOrRothPct

	ContribIRA RegOrRothPct
}

/*
Parses a YAML file containing scenario information into scenario objects which can be used when making projections
 */
func GetScenarios(scenariosFilepath string) ([]Scenario, error) {
	fileBytes, err := ioutil.ReadFile(scenariosFilepath)
	if err != nil {
		return nil, stacktrace.Propagate(err, "An IO error occurred reading scenarios file '%v'", scenariosFilepath)
	}

	var result []Scenario
	if err := yaml.Unmarshal(fileBytes, &result); err != nil {
		return nil, stacktrace.Propagate(err, "An error occurred deserializing the scenarios file contents")
	}
	return result, nil
}