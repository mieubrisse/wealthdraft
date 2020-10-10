package gov_constant_parser

type TaxBracket struct {
	Floor float64
	Rate float64
}

type AMTConstants struct {
	Exemption float64
	ExemptionPhaseoutFloor float64
	ExemptionPhaseoutRate float64
	LowEarnerRate float64
	HighEarnerRate float64
}

type FICAConstants struct {
	SocialSecurityWageCap float64
	SocialSecurityRate float64
	MedicareBaseRate float64
	MedicareSurtaxFloor float64
	MedicareSurtaxExtraRate float64
}

type ForeignConstants struct {
	ForeignEarnedIncomeExemption float64
}

type RetirementConstants struct {
	Personal401kContribLimit float64
	Total401kContribLimit float64
	IraContribLimit float64
	TradIraDeductiblePhaseoutFloor float64
	TradIraDeductiblePhaseoutCeiling float64
}

type GovConstantsForYear struct {
	Year int
	AMT AMTConstants
	FederalIncomeBrackets []TaxBracket
	FederalLTCGBrackets []TaxBracket
	FICA FICAConstants
	Foreign ForeignConstants
	Retirement RetirementConstants
	StandardDeduction float64
}
