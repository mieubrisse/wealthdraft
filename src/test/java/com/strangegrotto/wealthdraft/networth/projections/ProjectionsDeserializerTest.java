package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.networth.assets.AssetsFiles;
import com.strangegrotto.wealthdraft.networth.assets.AssetsWithHistory;
import com.strangegrotto.wealthdraft.networth.assets.ImmAssetsWithHistory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

public class ProjectionsDeserializerTest {
    @Test
    public void testPinnedDate() {
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("2020-10-31");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(LocalDate.of(2020, 10, 31), parsedDateOrErr.getVal());
    }

    @Test
    public void testRelativeMonths() {
        var today = LocalDate.now();
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("+3m");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(today.plusMonths(3), parsedDateOrErr.getVal());
    }

    @Test
    public void testRelativeYears() {
        var today = LocalDate.now();
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("+3y");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(today.plusYears(3), parsedDateOrErr.getVal());
    }

    @Test
    public void testInvalidStr() {
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("3y");
        Assert.assertTrue("Parsing of invalid string should have failed", parsedDateOrErr.hasGerr());
    }

    @Test
    public void testValidDeserialization() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.EXAMPLE);
        var projectionScenariosOrErr = projections.getScenarios();
        var projectionScenarios = new HashMap<String, ProjectionScenario>();
        for (var scenarioId : projectionScenariosOrErr.keySet()) {
            var scenarioOrErr = projectionScenariosOrErr.get(scenarioId);
            Assert.assertFalse(
                    "Scenario " + scenarioId + " deserialized with an error when it should have deserialized successfully",
                    scenarioOrErr.hasGerr()
            );
            projectionScenarios.put(scenarioId, scenarioOrErr.getVal());
        }

        Assert.assertEquals(ExpectedExampleProjections.EXPECTED_SCENARIOS, projectionScenarios);
    }

    @Test
    public void testInvalidYmlThrowsException() throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = AssetsFiles.EXAMPLE.getResource();
        AssetsWithHistory assetsWithHistory;
        assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = ProjectionsFiles.INVALID_YML.getResource();
        Main.addDeserializersNeedingAssets(mapper, assetsWithHistory.getAssets());
        try {
            mapper.readValue(projectionsUrl, Projections.class);
            Assert.fail("Deserialization of projections should have failed due to invalid YAML but didn't");
        } catch (IOException ignored) {}
    }

    @Test
    public void testDeserializationWithNonexistentAssets() throws IOException {
        var mapper = Main.getObjectMapper();

        // Empty assets this time, so all projections will correspond to nonexistent assets
        var assetsWithHistory = ImmAssetsWithHistory.builder().build();

        var projectionsUrl = ProjectionsFiles.EXAMPLE.getResource();

        Main.addDeserializersNeedingAssets(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

        var projectionScenariosOrErr = projections.getScenarios();
        for (var scenarioId : projectionScenariosOrErr.keySet()) {
            var scenarioOrErr = projectionScenariosOrErr.get(scenarioId);
            Assert.assertTrue(
                    "Scenario " + scenarioId + " should have failed to deserialize due to referencing nonexistent assets",
                    scenarioOrErr.hasGerr()
            );
        }
    }

    @Test
    public void testErrorOnPastDate() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.PAST_DATE_IN_PROJECTION);

        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedExampleProjections.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to a date in the past, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testNoErrorOnToday() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.CHANGE_ON_TODAY);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedExampleProjections.SELL_ALL_BTC_3Y_ID);
        Assert.assertFalse(
                "Expected scenario to pass parsing, but it failed",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorOnDependencyCycle() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.DEPENDENCY_CYLE);
        var projectionScenariosOrErr = projections.getScenarios();
        for (String scenarioId : projectionScenariosOrErr.keySet()) {
            var scenarioOrErr = projectionScenariosOrErr.get(scenarioId);
            Assert.assertTrue(
                    "Expected scenario '" + scenarioId + "' to fail parsing due to a date in the past, but it succeeded",
                    scenarioOrErr.hasGerr()
            );
        }
    }

    @Test
    public void testErrorOnTwoChangesOnSameDate() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.TWO_CHNAGES_ON_SAME_DATE);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedExampleProjections.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to two changes on the same date, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorOnTwoChangesOnSameDateFromDiffScenarios() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.TWO_CHANGES_ON_SAME_DATE_FROM_DIFF_SCENARIOS);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedExampleProjections.SELL_OTHER_HALF_BTC_2Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to two changes on the same date from different scenarios, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorWhenDependencyHasError() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.DEPENDENCY_HAS_ERROR);
        var projectionScenariosOrErr = projections.getScenarios();
        for (String scenarioId : projectionScenariosOrErr.keySet()) {
            var scenarioOrErr = projectionScenariosOrErr.get(scenarioId);
            switch (scenarioId) {
                case ExpectedExampleProjections.SELL_HALF_BTC_1Y_ID:
                    Assert.assertTrue(
                            "Expected scenario '" + scenarioId + "' to fail parsing due to the intentionally-induced error, but it succeeded",
                            scenarioOrErr.hasGerr()
                    );
                    break;
                case ExpectedExampleProjections.SELL_OTHER_HALF_BTC_2Y_ID:
                    Assert.assertTrue(
                            "Expected scenario '" + scenarioId + "' to fail parsing due to its dependency having an error, but it succeeded",
                            scenarioOrErr.hasGerr()
                    );
                    break;
                default:
                    throw new RuntimeException("Unrecognized scenario ID; this is a bug with the test");
            }
        }
    }

    @Test
    public void testErrorWhenDependingOnNonexistentScenario() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFiles.DEPEND_ON_NONEXISTENT_SCENARIO);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedExampleProjections.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due depending on a nonexistent scenario, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    private static Projections parseProjectionsFile(ProjectionsFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = AssetsFiles.EXAMPLE.getResource();
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);
        Main.addDeserializersNeedingAssets(mapper, assetsWithHistory.getAssets());

        var projectionsUrl = testFile.getResource();
        return mapper.readValue(projectionsUrl, Projections.class);
    }
}
