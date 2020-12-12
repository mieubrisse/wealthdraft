## TBD
* Refactor the data model around assets in preparation for being able to define per-asset growth rates
* Split net worth rendering into its own class to control complexity

## 0.5.0
* Allow users to base scenarios on other scenarios

## 0.4.0
* Rename `GError` -> `Gerr` and `ValueOrGError` -> `ValOrGerr` to be easier to write
* Rename `Gerr.newError` -> `Gerr.newGerr` and `Gerr.propagate` -> `Gerr.propGerr` to take up less space in code
* Rename the following `ValOrGerr` methods to be easier to write:
    * `getValue` -> `getVal`
    * `hasGError` -> `hasGerr`
    * `getError` -> `getGerr`
    * `ofValue` -> `val`
    * `newError` -> `newGerr`
    * `propagateErr` -> `propGerr`
* Flag error if an asset change tries to subtract more funds from an asset than exist
* Throw an error for scenarios who have a date in the past

## 0.3.0
* Render only scenarios with year >= currentYear, and provide an `--all` flag to render all scenarios
* Fix bug that warns user about stale gov constants when the latest gov constants are from a future year
* Allow user to specify an assets YAML with the `--assets` flag for printing historical net worth over time
* Allow user to specify a projections YAML with the `--projections` flag for specifying hypothetical scenarios
* Break out the main function into several smaller helper functions to make it easier to read
* Rename `Scenario` to `TaxScenario` to more accurately reflect its purpose
* Increase the space allotted for currency-printing to 10 chars (up from 9) to allow for 8-figure sums

## 0.2.0
* Explain what use cases are handled now, and which ones aren't
* Added example image of the CLI
* Extract the tax-calculating module into something that could be included as a library in another Java program

## 0.1.7
* Build JAR for Java 8
* Add 2019 gov constants

## 0.1.6
* Polish up documentation

## 0.1.5
* Add "Total Taxes" section
* Add unearned income section to scenario
* Corrected NIIT calculation
* Refactored to use distinctions of `earnedIncome`, `nonPreferentialUnearnedIncome`, and `preferentialUnearnedIncome`

## 0.1.4
* Let scenarios specify which gov constant years they're going to use

## 0.1.3
* Add AMT calculator
* Correct regular income tax calculator
* Bunch of formatting cleanup

## 0.1.2
* Add changelog
