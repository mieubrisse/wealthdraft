package progressive_tax_calculator

import (
	"github.com/mieubrisse/financial-predictor/tax_bracket_parser"
	"github.com/sirupsen/logrus"
	"math"
	"sort"
)

func Calculate(brackets []tax_bracket_parser.TaxBracket, amount float64) float64 {
	// Sort the brackets in REVERSE order
	comesBeforeFunc := func(firstElemIdx int, secondElemIdx int) bool {
		return brackets[firstElemIdx].Floor > brackets[secondElemIdx].Floor
	}
	sort.Slice(brackets, comesBeforeFunc)

	var totalTax float64 = 0
	for _, bracket := range(brackets) {
		floor := bracket.Floor
		rate := bracket.Rate
		logrus.Debugf("Floor: %v, Rate: %v", floor, rate)
		amountInBracket := math.Max(0, amount - floor)
		totalTax += rate * amountInBracket
		amount -= amountInBracket
		logrus.Debugf("Tax so far: %v", totalTax)
	}
	return totalTax
}
