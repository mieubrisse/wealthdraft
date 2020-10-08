package gov_constant_parser

import (
	"github.com/go-yaml/yaml"
	"github.com/palantir/stacktrace"
	"io/ioutil"
)

type YearlyGovConstants struct {

}

/*
Parses a YAML file containing scenario information into scenario objects which can be used when making projections
*/
func GetGovConstants(govConstantsFilepath string) (map[int]GovConstantsForYear, error) {
	fileBytes, err := ioutil.ReadFile(govConstantsFilepath)
	if err != nil {
		return nil, stacktrace.Propagate(err, "An IO error occurred reading gov constants file '%v'", govConstantsFilepath)
	}

	var result map[int]GovConstantsForYear
	if err := yaml.Unmarshal(fileBytes, &result); err != nil {
		return nil, stacktrace.Propagate(err, "An error occurred deserializing the scenarios file contents")
	}

	return result, nil
}
