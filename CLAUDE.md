# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Wealthdraft
===========
A Java 11 CLI tax calculator and financial planning tool. Computes federal income tax (regular and AMT), employment tax (FICA), Net Investment Income Tax, deductions (401k, IRA, standard), foreign-earned income exclusion, asset allocations, and multi-year net worth projections.

Build & Development Commands
-----------------------------
```bash
# Build (creates fat JAR in build/libs/)
./gradlew jar

# Run all tests (JUnit 4)
./gradlew test

# Run a single test class
./gradlew test --tests com.strangegrotto.wealthdraft.tax.RegularIncomeTaxCalculatorTest

# Run a single test method
./gradlew test --tests com.strangegrotto.wealthdraft.tax.RegularIncomeTaxCalculatorTest.methodName

# Build and run CLI with example data
./scripts/build_and_run.sh

# SpotBugs static analysis (HTML reports in build/reports/spotbugs/)
./gradlew spotbugsMain spotbugsTest
```

Architecture
------------
**Entry point:** `com.strangegrotto.wealthdraft.Main` — parses CLI args (Argparse4j), deserializes YAML config files (Jackson), runs calculations, and renders output tables (Picnic).

**Input:** Seven YAML files define the financial model: `gov-constants.yml` (tax brackets/rates by year), `scenarios.yml` (tax scenarios), `assets.yml` (asset definitions), `assets-history.yml` (historical values), `projections.yml` (future projections), `asset-allocations.yml` (target allocations), `filters.yml` (asset filtering rules). Example files live in `src/test/resources/examples/`.

**Key packages** (under `com.strangegrotto.wealthdraft`):

- `tax/` — Tax calculators: regular income, AMT, FICA, progressive bracket engine. `TaxCalculator` orchestrates the full computation.
- `assetallocation/` — Compares actual vs. target asset allocations.
- `assetfilters/` — Composable filters (conjunction, disjunction, negation, tag-based) for selecting subsets of assets.
- `assetimpls/` — Concrete asset types (BankAccount, Stock, etc.).
- `networth/history/` — Asset history tracking and net worth over time.
- `networth/projections/` — Multi-year financial projections.
- `govconstants/` — Year-specific tax constants and bracket definitions.
- `errors/` — Custom error handling: `Gerr` (error type) and `ValOrGerr` (Result/Either monad for error propagation).
- `validator/` — Input validation and deserialization utilities.

**Design patterns:**
- **Immutables** library for value objects (classes annotated with `@Value.Immutable`).
- **`ValOrGerr<T>`** monad for propagating errors without exceptions — used throughout deserialization and calculation code.
- **Composable filters** — `ConjunctionAssetFilter`, `DisjunctionAssetFilter`, `NegationAssetFilter` combine via AND/OR/NOT logic.
- **Custom Jackson deserializers** for domain objects (tax brackets, asset definitions, projections).

Key Dependencies
-----------------
- Jackson 2.12.0 (YAML/JSON), Guava 28.2, Argparse4j 0.8.1, Immutables 2.8.2, Picnic 0.5.0 (table rendering), SpotBugs 4.6.0
- Dependency versions defined in `gradle.properties`

CI
--
CircleCI builds on PRs and publishes GitHub releases on semver tags (format: `X.Y.Z`). Config in `.circleci/config.yml`.
