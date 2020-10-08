package tax_bracket_parser

import (
	"encoding/csv"
	"github.com/palantir/stacktrace"
	"os"
	"strconv"
)

const (
	yearColIndex = 0
	floorColIndex = 1
	rateColIndex = 2

	floatBitSize = 64
)

type TaxBracket struct {
	Floor float64
	Rate float64
}

/*
Reads a CSV filepath with a history of tax brackets and gets the latest one
 */
func GetLatestBrackets(bracketsFilepath string) ([]TaxBracket, error) {
	fp, err := os.Open(bracketsFilepath)
	if err != nil {
		return nil, stacktrace.Propagate(err, "An IO error occurred opening the bracket file")
	}

	csvReader := csv.NewReader(fp)
	records, err := csvReader.ReadAll()
	if err != nil {
		return nil, stacktrace.Propagate(err, "An error occurred reading the records of the tax brackets CSV file")
	}

	taxBracketsByYear := make(map[int][]TaxBracket)
	mostRecentYear := 0
	for idx, row := range(records) {
		// Skip the header line
		if idx == 0 {
			continue
		}

		yearStr := row[yearColIndex]
		yearInt64, err := strconv.ParseInt(yearStr, 10, 0)
		if err != nil {
			return nil, stacktrace.Propagate(err, "An error occurred converting year string '%v' to int64", yearStr)
		}
		year := int(yearInt64)
		if year > mostRecentYear {
			mostRecentYear = year
		}

		taxBrackets, found := taxBracketsByYear[year]
		if !found {
			taxBrackets = []TaxBracket{}
		}

		bracket, err := parseRowToTaxBracket(row)
		if err != nil {
			return nil, stacktrace.Propagate(err, "An error occurred parsing a row '%v' to a tax bracket", row)
		}
		taxBrackets = append(taxBrackets, *bracket)
		taxBracketsByYear[year] = taxBrackets
	}

	return taxBracketsByYear[mostRecentYear], nil
}

func parseRowToTaxBracket(row []string) (*TaxBracket, error) {
	floorStr := row[floorColIndex]
	rateStr := row[rateColIndex]

	floor, err := strconv.ParseFloat(floorStr, floatBitSize)
	if err != nil {
		return nil, stacktrace.Propagate(err, "An error occurred converting floor string '%v' to float64", floorStr)
	}
	rate, err := strconv.ParseFloat(rateStr, floatBitSize)
	if err != nil {
		return nil, stacktrace.Propagate(err, "An error occurred converting rate string '%v' to float64", rateStr)
	}

	return &TaxBracket{
		Floor: floor,
		Rate: rate,
	}, nil
}
