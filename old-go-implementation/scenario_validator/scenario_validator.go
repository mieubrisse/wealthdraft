package scenario_validator

import (
	"github.com/mieubrisse/financial-predictor/gov_constant_parser"
	"github.com/mieubrisse/financial-predictor/scenario_parser"
	"github.com/palantir/stacktrace"
)

type valueGetter func(scenario scenario_parser.Scenario) float64
type check func(string, float64) error

type fieldChecks struct {
	getter valueGetter
	checks []check
}

// Verifies that the given value is 0 <= x <= 1
func checkIsRatio(fieldName string, value float64) error {
	if value < 0 || value > 1 {
		return stacktrace.NewError("Invalid value: %v is a ratio, and must be in range [0,1]", fieldName)
	}
	return nil
}

var perFieldValidators map[string]fieldChecks = map[string]fieldChecks{
	"traditional IRA contribution": {
		getter:  func(scenario scenario_parser.Scenario) float64 { return scenario.ContribIRA.Regular },
		checks: []check {
			checkIsRatio,
		},
	},
	"Roth IRA contribution": {
		getter: func(scenario scenario_parser.Scenario) float64 { return scenario.ContribIRA.Roth },
		checks: []check {
			checkIsRatio,
		},
	},
	"traditional 401k contribution": {
		getter: func(scenario scenario_parser.Scenario) float64 { return scenario.Contrib401k.Regular },
		checks: []check {
			checkIsRatio,
		},
	},
	"Roth 401k contribution": {
		getter: func(scenario scenario_parser.Scenario) float64 { return scenario.Contrib401k.Roth },
		checks: []check {
			checkIsRatio,
		},
	},
}

func ValidateScenario(scenario scenario_parser.Scenario, constants gov_constant_parser.GovConstantsForYear) error {
	for fieldName, checksConfig := range perFieldValidators {
		fieldValue := checksConfig.getter(scenario)
		for _, check := range checksConfig.checks {
			if err := check(fieldName, fieldValue); err != nil {
				return stacktrace.Propagate(err, "An error occurred validating field '%v'", fieldName)
			}
		}
	}

	// Verify 401k and IRA contribution limits aren't overstepped
	sum401kContributionRatio := scenario.Contrib401k.Roth + scenario.Contrib401k.Regular
	if sum401kContributionRatio > 1 {
		return stacktrace.NewError(
			"Total 401k contribution is %v of the contribution limit; should be in range [0, 1]",
			sum401kContributionRatio)
	}
	sumIRAContributionRatio := scenario.ContribIRA.Roth + scenario.ContribIRA.Regular
	if sumIRAContributionRatio > 1 {
		return stacktrace.NewError(
			"Total IRA contribution is %v of the contribution limit; should be in range [0, 1]",
			sumIRAContributionRatio)
	}

	return nil
}
