/* * This Java source file was generated by the Gradle 'init' task. */
package com.strangegrotto.wealthdraft;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Ordering;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitions;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.govconstants.RetirementConstants;
import com.strangegrotto.wealthdraft.networth.NetWorthRenderer;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.networth.history.AssetsHistory;
import com.strangegrotto.wealthdraft.assets.temporal.AssetParameterChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetParameterChangeDeserializer;
import com.strangegrotto.wealthdraft.networth.history.AssetsHistoryDeserializer;
import com.strangegrotto.wealthdraft.networth.projections.Projections;
import com.strangegrotto.wealthdraft.networth.projections.ProjectionsDeserializer;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.TaxScenario;
import com.strangegrotto.wealthdraft.tax.ScenarioTaxCalculator;
import com.strangegrotto.wealthdraft.tax.ScenarioTaxes;
import com.strangegrotto.wealthdraft.tax.Tax;
import com.strangegrotto.wealthdraft.validator.ValidationWarning;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final int SUCCESS_EXIT_CODE = 0;
    private static final int FAILURE_EXIT_CODE = 1;

    // TODO Rename this to "tax-scenarios"??? The reason to hold off is to combine the "projections"
    //  and "scenarios" files into a single one
    private static final String TAX_SCENARIOS_FILEPATH_ARG = "scenarios";
    private static final String GOV_CONSTANTS_FILEPATH_ARG = "gov-constants";
    private static final String ASSETS_FILEPATH_ARG = "assets";
    private static final String ASSETS_HISTORY_FILEPATH_ARG = "assets-history";
    private static final String PROJECTIONS_FILEPATH_ARG = "projections";
    private static final String LOG_LEVEL_ARG = "log-level";
    private static final String ALL_SCENARIOS_ARG = "all";

    private static final String LOGBACK_LAYOUT_PATTERN = "%highlight(%-5level) %logger{0} - %message%n";

    private static final int MINIMUM_ITEM_TITLE_WIDTH = 40;
    private static final int MINIMUM_CURRENCY_WIDTH = 10;
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat  ("###,##0");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.0%");

    private static final int MAX_YEARS_TO_PROJECT = 30;

    // TODO Make this months instead
    private static final int PROJECTION_DISPLAY_INCREMENT_YEARS = 1;

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Logger slf4jRootLogger = org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        ch.qos.logback.classic.Logger logbackRootLogger = (ch.qos.logback.classic.Logger) slf4jRootLogger;
        configureRootLoggerPattern(logbackRootLogger);

        ArgumentParser parser = ArgumentParsers.newFor("Financial Predictor").build()
                .defaultHelp(true)
                .description("A financial modelling CLI");
        parser.addArgument("--" + TAX_SCENARIOS_FILEPATH_ARG)
                .dest(TAX_SCENARIOS_FILEPATH_ARG)
                .required(true)
                .help("YAML file containing scenarios to calculate");
        // TODO rename this; it's only tax-specific
        parser.addArgument("--" + ALL_SCENARIOS_ARG)
                .dest(ALL_SCENARIOS_ARG)
                .type(Boolean.class)
                .setDefault(Boolean.FALSE)
                .action(Arguments.storeTrue())
                .help("If set, renders all scenarios (rather than just the current and future years)");
        parser.addArgument("--" + GOV_CONSTANTS_FILEPATH_ARG)
                .dest(GOV_CONSTANTS_FILEPATH_ARG)
                .required(true)
                .help("YAML file of gov constants per year");
        parser.addArgument("--" + ASSETS_FILEPATH_ARG)
                .dest(ASSETS_FILEPATH_ARG)
                .required(true)
                .help("YAML file of asset definitions");
        parser.addArgument("--" + ASSETS_HISTORY_FILEPATH_ARG)
                .dest(ASSETS_HISTORY_FILEPATH_ARG)
                .required(true)
                .help("YAML file of historical asset values over time");
        parser.addArgument("--" + PROJECTIONS_FILEPATH_ARG)
                .dest(PROJECTIONS_FILEPATH_ARG)
                .required(true)
                .help("YAML file of future projections");
        parser.addArgument("--" + LOG_LEVEL_ARG)
                .dest(LOG_LEVEL_ARG)
                .type(Level.class)
                .setDefault(Level.INFO)
                .choices(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR)
                .help("Log level to output at");

        Namespace parsedArgs;
        try {
            parsedArgs = parser.parseArgs(args);
        } catch (HelpScreenException e) {
            // For some strange reason, argparse4j throws an exception on help
            System.exit(SUCCESS_EXIT_CODE);
            return;
        } catch (ArgumentParserException e) {
            log.error("An error occurred parsing the CLI args", e);
            System.exit(FAILURE_EXIT_CODE);
            return;
        }

        Level logLevel = parsedArgs.get(LOG_LEVEL_ARG);
        logbackRootLogger.setLevel(logLevel);

        var mapper = getObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();

        String taxScenariosFilepath = parsedArgs.getString(TAX_SCENARIOS_FILEPATH_ARG);
        log.debug("Scenarios filepath: {}", taxScenariosFilepath);
        MapType taxScenariosMapType = typeFactory.constructMapType(HashMap.class, String.class, TaxScenario.class);
        Map<String, TaxScenario> taxScenarios;
        try {
            taxScenarios = mapper.readValue(new File(taxScenariosFilepath), taxScenariosMapType);
        } catch (IOException e) {
            log.error("An error occurred parsing tax scenarios file '{}'", taxScenariosFilepath, e);
            System.exit(FAILURE_EXIT_CODE);
            return;
        }

        String govConstantsFilepath = parsedArgs.getString(GOV_CONSTANTS_FILEPATH_ARG);
        log.debug("Gov constants filepath: {}", govConstantsFilepath);
        MapType govConstantsMapType = typeFactory.constructMapType(HashMap.class, Integer.class, GovConstantsForYear.class);
        Map<Integer, GovConstantsForYear> allGovConstants;
        try {
            allGovConstants = mapper.readValue(new File(govConstantsFilepath), govConstantsMapType);
        } catch (IOException e) {
            log.error("An error occurred parsing gov constants file '{}'", govConstantsFilepath, e);
            System.exit(FAILURE_EXIT_CODE);
            return;
        }

        String assetsFilepath = parsedArgs.getString(ASSETS_FILEPATH_ARG);
        log.debug("Assets filepath: {}", assetsFilepath);
        AssetDefinitions assetDefinitions;
        try {
            assetDefinitions = mapper.readValue(new File(assetsFilepath), AssetDefinitions.class);
        } catch (IOException e) {
            log.error("An error occurred parsing the assets file '{}'", assetsFilepath, e);
            System.exit(FAILURE_EXIT_CODE);
            return;
        }

        addDeserializersNeedingAssets(mapper, assetDefinitions.getAssets());

        String assetsHistoryFilepath = parsedArgs.getString(ASSETS_HISTORY_FILEPATH_ARG);
        log.debug("Assets history filepath: {}", assetsHistoryFilepath);
        AssetsHistory assetsHistory;
        try {
            assetsHistory = mapper.readValue(new File(assetsHistoryFilepath), AssetsHistory.class);
        } catch (IOException e) {
            log.error("An error occurred parsing the assets history file '{}'", assetsHistoryFilepath, e);
            System.exit(FAILURE_EXIT_CODE);
            return;
        }

        String projectionsFilepath = parsedArgs.getString(PROJECTIONS_FILEPATH_ARG);
        log.debug("Projections filepath: {}", assetsFilepath);
        Projections projections;
        try {
            projections = mapper.readValue(new File(projectionsFilepath), Projections.class);
        } catch (IOException e) {
            log.error("An error occurred parsing the projections file '{}'", projectionsFilepath, e);
            System.exit(FAILURE_EXIT_CODE);
            return;
        }

        Display display = new Display(
                log,
                MINIMUM_ITEM_TITLE_WIDTH,
                MINIMUM_CURRENCY_WIDTH,
                CURRENCY_FORMAT);
        renderMultipleTaxScenarios(
                display,
                parsedArgs.getBoolean(ALL_SCENARIOS_ARG),
                taxScenarios,
                allGovConstants
        );

        var netWorthRenderer = new NetWorthRenderer(display, PROJECTION_DISPLAY_INCREMENT_YEARS, MAX_YEARS_TO_PROJECT);
        var emptyOrErr =netWorthRenderer.renderNetWorthCalculations(assetsHistory, projections);
        if (emptyOrErr.hasGerr()) {
            log.error("An error occurred rendering net worth: {}", emptyOrErr.getGerr());
            System.exit(FAILURE_EXIT_CODE);
        }
    }

    @VisibleForTesting
    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());    // Support deserializing to Optionals

        var deserializerModule = new SimpleModule();
        deserializerModule.addDeserializer(AssetParameterChange.class, new AssetParameterChangeDeserializer());
        mapper.registerModule(deserializerModule);

        return mapper;
    }

    @VisibleForTesting
    public static void addDeserializersNeedingAssets(ObjectMapper mapper, Map<String, Asset> assets) {
        var deserializerModule = new SimpleModule();
        deserializerModule.addDeserializer(Projections.class, new ProjectionsDeserializer(assets));
        deserializerModule.addDeserializer(AssetsHistory.class, new AssetsHistoryDeserializer(assets));
        mapper.registerModule(deserializerModule);
    }

    private static void configureRootLoggerPattern(ch.qos.logback.classic.Logger rootLogger) {
        // Configure the logger with our desired pattern
        // See: http://logback.qos.ch/manual/layouts.html
        LoggerContext loggerContext = rootLogger.getLoggerContext();
        loggerContext.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(LOGBACK_LAYOUT_PATTERN);
        encoder.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.start();

        rootLogger.addAppender(appender);
    }

    private static void renderMultipleTaxScenarios(
            Display display,
            boolean doRenderAllScenarios,
            Map<String, TaxScenario> scenarios,
            Map<Integer, GovConstantsForYear> allGovConstants) {
        Integer latestYear = Collections.max(allGovConstants.keySet());
        GovConstantsForYear latestGovConstants = allGovConstants.get(latestYear);
        int currentYear = Year.now().getValue();
        if (currentYear > latestYear) {
            log.warn("The latest gov constants we have are old, from {}!!!", latestYear);
        }

        // Render scenarios
        // TODO break this into a helper function
        int minYearToRender = doRenderAllScenarios ? 0 : Year.now().getValue();
        List<String> scenarioNames = new ArrayList<>(scenarios.keySet());
        scenarioNames.sort(Ordering.natural());
        for (String scenarioName : scenarioNames) {
            TaxScenario scenario = scenarios.get(scenarioName);
            if (scenario.getYear() < minYearToRender) {
                continue;
            }

            display.printEmptyLine();
            display.printBannerHeader(scenarioName);

            int scenarioYear = scenario.getYear();
            GovConstantsForYear govConstantsToUse;
            if (scenarioYear > currentYear) {
                log.info(
                        "Scenario wants future year {}; using latest gov constants from {}",
                        scenarioYear,
                        latestYear
                );
                govConstantsToUse = latestGovConstants;
            } else {
                if (!allGovConstants.containsKey(scenarioYear)) {
                    log.error(
                            "Could not calculate scenario; scenario wants year {} but no gov constants for that year were defined",
                            scenarioYear
                    );
                    continue;
                }
                govConstantsToUse = allGovConstants.get(scenarioYear);
            }

            // Errors
            ValOrGerr<List<ValidationWarning>> validationResult = validateTaxScenarioAgainstGovConstants(scenario, govConstantsToUse);
            if (validationResult.hasGerr()) {
                log.error(validationResult.getGerr().toString());
                continue;
            }

            // Warnings
            List<ValidationWarning> validationWarnings = validationResult.getVal();
            if (validationWarnings.size() > 0) {
                for (ValidationWarning warning : validationWarnings) {
                    log.warn(warning.getMessage());
                }
            }

            renderTaxScenario(display, scenario, govConstantsToUse);
        }
    }


    private static ValOrGerr<List<ValidationWarning>> validateTaxScenarioAgainstGovConstants(TaxScenario scenario, GovConstantsForYear govConstantsForYear) {
        IncomeStreams grossIncomeStreams = scenario.getIncomeStreams();

        List<ValidationWarning> warnings = new ArrayList<>();

        RetirementConstants retirementConstants = govConstantsForYear.getRetirementConstants();
        long totalIraContrib = scenario.getIraContrib().getTrad() + scenario.getIraContrib().getRoth();
        if (totalIraContrib > retirementConstants.getIraContribLimit()) {
            return ValOrGerr.newGerr(
                    "The IRA contribution limit is {} but the scenario's total IRA contribution is {}",
                    retirementConstants.getIraContribLimit(),
                    totalIraContrib
            );
        }
        if (totalIraContrib < retirementConstants.getIraContribLimit()) {
            warnings.add(ValidationWarning.of(
                    "The IRA contribution limit {} but the scenario's total IRA contribution is only {}",
                    retirementConstants.getIraContribLimit(),
                    totalIraContrib
            ));
        }

        long trad401kContrib = scenario.get401kContrib().getTrad();
        long total401kContrib = trad401kContrib + scenario.get401kContrib().getRoth();
        if (total401kContrib > govConstantsForYear.getRetirementConstants().getPersonal401kContribLimit()) {
            return ValOrGerr.newGerr(
                    "The 401k contribution limit is {} but the scenario's total 401k contribution is {}",
                    retirementConstants.getPersonal401kContribLimit(),
                    total401kContrib
            );
        }
        if (total401kContrib < retirementConstants.getPersonal401kContribLimit()) {
            warnings.add(ValidationWarning.of(
                    "The IRA contribution limit {} but the scenario's total 401k contribution is only {}",
                    retirementConstants.getPersonal401kContribLimit(),
                    total401kContrib
            ));
        }

        // IRA contributions MUST be done with earned income, and trad 401k contributions can
        //  only be done via an employer (i.e. earned income) so total_ira_contrib + trad_401k_contrib must be
        // See: https://www.investopedia.com/retirement/ira-contribution-limits/
        if (grossIncomeStreams.getEarnedIncome() < totalIraContrib + trad401kContrib) {
            return ValOrGerr.newGerr(
                    "IRA contributions are limited to earned income and trad 401k contributions can only be done " +
                            "using earned income so total_ira_contrib + trad_401k_contrib must be < earned_income, but" +
                            "earned_income {} is < total_ira_contrib {} + trad_401k_contrib {}",
                    grossIncomeStreams.getEarnedIncome(),
                    totalIraContrib,
                    trad401kContrib
            );
        }

        return ValOrGerr.val(warnings);
    }

    private static void renderTaxScenario(
            Display display,
            TaxScenario scenario,
            GovConstantsForYear govConstants) {
        display.printEmptyLine();
        display.printSectionHeader("RETIREMENT");
        display.printCurrencyItem("Trad 401k Contrib", scenario.get401kContrib().getTrad());
        display.printCurrencyItem("Roth 401k Contrib", scenario.get401kContrib().getRoth());
        display.printCurrencyItem("Trad IRA Contrib", scenario.getIraContrib().getTrad());
        display.printCurrencyItem("Roth IRA Contrib", scenario.getIraContrib().getRoth());

        IncomeStreams grossIncomeStreams = scenario.getIncomeStreams();

        display.printEmptyLine();
        display.printSectionHeader("GROSS INCOME");
        display.printCurrencyItem("Earned Income", grossIncomeStreams.getEarnedIncome());
        display.printCurrencyItem("Non-Preferential Unearned Income", grossIncomeStreams.getNonPreferentialUnearnedIncome());
        display.printCurrencyItem("Preferential Earned Income", grossIncomeStreams.getPreferentialUnearnedIncome());
        display.printSumLine();
        long grossIncome = grossIncomeStreams.getTotal();
        display.printCurrencyItem("Gross Income", grossIncome);

        long totalAmtAdjustments = scenario.getAmtAdjustments().stream()
                .reduce(0L, Long::sum);
        display.printEmptyLine();
        display.printSectionHeader("ADJUSTMENTS");
        display.printCurrencyItem("AMT Adjustments", totalAmtAdjustments);

        ScenarioTaxes taxes = ScenarioTaxCalculator.calculateScenarioTax(scenario, govConstants);
        Map<Tax, Double> ficaTaxes = taxes.getFicaTaxes();
        Map<Tax, Double> primarySystemTaxes = taxes.getPrimarySystemIncomeTaxes();
        Map<Tax, Double> amtTaxes = taxes.getAmtTaxes();

        display.printEmptyLine();
        display.printSectionHeader("FICA TAX");
        renderTaxesSection(display, "FICA Tax", ficaTaxes, grossIncome);

        display.printEmptyLine();
        display.printSectionHeader("REG FED INCOME TAX");
        renderTaxesSection(display, "Reg Fed Income Tax", primarySystemTaxes, grossIncome);

        display.printEmptyLine();
        display.printSectionHeader("AMT");
        renderTaxesSection(display, "AMT", amtTaxes, grossIncome);

        Map<Tax, Double> totalTaxes = new HashMap<>(ficaTaxes);
        String higherTaxSystem;
        String lowerTaxSystem;
        if (taxes.isPrimarySystemHigher()) {
            higherTaxSystem = "regular income tax";
            lowerTaxSystem = "AMT";
            totalTaxes.putAll(primarySystemTaxes);
        } else {
            higherTaxSystem = "AMT";
            lowerTaxSystem = "regular income tax";
            totalTaxes.putAll(amtTaxes);
        }

        display.printEmptyLine();
        display.printSectionHeader("TOTAL TAX");
        log.info(
                "Year's {} tax system was higher than {}; using {} income tax",
                higherTaxSystem,
                lowerTaxSystem,
                higherTaxSystem
        );
        renderTaxesSection(display, "Scenario Tax", totalTaxes, grossIncome);
    }



    private static void renderTaxesSection(Display display, String titleCaseSumName, Map<Tax, Double> taxes, long grossIncome) {
        double totalTaxes = 0D;

        List<Tax> displayOrder = taxes.keySet().stream()
                .sorted(Comparator.comparing(Tax::getPrettyName))
                .collect(Collectors.toList());

        for (Tax taxType : displayOrder) {
            double taxAmount = taxes.get(taxType);
            totalTaxes += taxAmount;
            String prettyName = taxType.getPrettyName();
            display.printCurrencyItem(prettyName, taxAmount);
        }
        double effectiveTaxRate = totalTaxes / (double)grossIncome;
        display.printSumLine();
        String itemTitle = "Total " + titleCaseSumName;
        log.info(
                "{}: {} ({} effective tax rate)",
                String.format("%1$" + MINIMUM_ITEM_TITLE_WIDTH + "s", itemTitle),
                String.format(
                        "%1$" + MINIMUM_CURRENCY_WIDTH + "s",
                        CURRENCY_FORMAT.format(totalTaxes)
                ),
                PERCENT_FORMAT.format(effectiveTaxRate)
        );
    }
}
