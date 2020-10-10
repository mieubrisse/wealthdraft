package gov_constant_parser

type TaxBracket struct {
	Floor float64	`yaml:"floor"`
	Rate float64	`yaml:"rate"`
}

type AMTConstants struct {
	Exemption float64	`yaml:"exemption"`
	ExemptionPhaseoutFloor float64	`yaml:"exemptionPhaseoutFloor"`
	ExemptionPhaseoutRate float64	`yaml:"exemptionPhaseoutRate"`
	LowEarnerRate float64	`yaml:"lowEarnersRate"`
	HighEarnerRate float64	`yaml:"highEarnersRate"`
}

type FICAConstants struct {
	SocialSecurityWageCap float64	`yaml:"socialSecurityWageCap"`
	SocialSecurityRate float64	`yaml:"socialSecurityRate"`
	MedicareBaseRate float64	`yaml:"medicareBaseRate"`
	MedicareSurtaxFloor float64	`yaml:"medicareSurtaxFloor"`
	MedicareSurtaxExtraRate float64	`yaml:"medicareSurtaxExtraRate"`

	// aka Unearned Income Medicare Contribution Surtax
	NetInvestmentIncomeTaxRate float64	`yaml:"netInvestmentIncomeTaxRate"`
	NetInvestmentIncomeThreshold float64	`yaml:"netInvestmentIncomeThreshold"`
}

type ForeignConstants struct {
	ForeignEarnedIncomeExemption float64	`yaml:"foreignEarnedIncomeExemption"`
}

type RetirementConstants struct {
	Personal401kContribLimit float64	`yaml:"personal401kContribLimit"`
	Total401kContribLimit float64	`yaml:"total401kContribLimit"`
	IRAContribLimit float64	`yaml:"iraContribLimit"`
	TradIraDeductiblePhaseoutFloor float64	`tradIRADeductiblePhaseoutFloor`
	TradIraDeductiblePhaseoutCeiling float64	`tradIRADeductiblePhaseoutCeiling`
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
