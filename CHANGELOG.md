**NOTE: This changelog has been abandoned after version 0.14.1. I'll reconstruct it if it's worth having a changelog again.**

## 0.14.1
* Added a `taxAlreadyPaid` section to tax scenarios, to also show exactly how much tax you'll be predicted to pay

## 0.14.0
* Split asset filters into their own file

## 0.13.0
* Remove negation filter now that default tags are available, to guarantee that filters strictly reduce the number of assets
* Add a new asset filter type, `filter`, which allows users to compose filters of other filters

## 0.12.2
* Color asset allocation rows based on their deviation status
* Allow users to set the default value of a tag if it's missing
* Remove the `Asset` class in favor of an `AssetType` enum

## 0.12.1
* Add extra tests to ensure nonsensical target asset allocation rules are rejected
* Add tests to make sure that the TagAssetFilter is using only recognized tags and values, to guard against typos
* Show numerator/denominator value in asset allocation chart

## 0.12.0
* Refactor assets history from `assetId -> (date -> assetSnapshot)` to `date -> (assetId -> assetSnapshot)`

## 0.11.0
* Allow full set math expressiveness when defining asset allocation filters: AND, NOT, OR, and tag filter
* Split up asset allocation calculating & rendering logic so the calculation logic can be tested
* Added deviation % and status to asset allocation table

## 0.10.0
* Allow users to configure the denominator of asset allocation rules

## 0.9.0
* Finally sorted out the generics puzzle!
* Add SpotBugs
* Give the ability to specify required custom tags when defining assets
* Allow users to define a target asset allocation, and print how close to it they are

## 0.8.0
* Refactor asset modelling further to make it easy to add new assets
* Add `StockAsset`
* Disallowed duplicate keys in JSON, and added a test to ensure it's blocked
* Sorted each asset's records in `AssetsHistory` by date 

## 0.7.8
* Remove debugging line in CircleCI config now that publishing works!

## 0.7.7
* Upgrade CircleCI Go Docker image to 1.15

## 0.7.6
* Use personal access token for pushing to repo

## 0.7.5
* Actually fix syntax error :|

## 0.7.4
* Fix syntax error in CircleCI config file

## 0.7.3
* Try using workspaces to pass artifacts between jobs

## 0.7.2
* More CircleCI debugging

## 0.7.1
* Trying again to publish artifacts on only X.Y.Z tags

## 0.7.0
* Add asset tags, for classifying assets when doing asset allocation calculations
* Remove `idea` plugin from `build.gradle`, as it's no longer needed per https://youtrack.jetbrains.com/issue/IDEA-257670
* Removed `AssetType` in favor of classes that implement `Asset` directly (e.g. `BankAccountAsset`)
* Reordered package structure to be more sensible
* Split deserialization & error-checking for `AssetsHistory` and `AssetDefinitions` to make code easier to manage
* Drop the `-example` suffix to all the example files
* Update CircleCI config to publish releases automatically

## 0.6.0
* Refactor the data model around assets in preparation for being able to define per-asset growth rates
* Split net worth rendering into its own class to control complexity
* Refactor the projections calculator to use the new asset datamodel
* Update to Java 11 to get the lovely `var` keyword
* Switch to using BigDecimal for all networth calculations
* Add custom `Immutables` style, and switch all networth calculation code over to it
* Add a deserializer with tests to `AssetsWithHistory`, so arbitrary asset history can be added

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
