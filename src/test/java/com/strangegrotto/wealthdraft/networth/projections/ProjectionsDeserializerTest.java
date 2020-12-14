package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.networth.AssetsWithHistory;
import com.strangegrotto.wealthdraft.networth.ImmutableAssetsWithHistory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ProjectionsDeserializerTest {
    @Test
    public void testPinnedDate() {
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("2020-10-31");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(LocalDate.of(2020, 10, 31), parsedDateOrErr.getVal());
    }

    @Test
    public void testRelativeMonths() throws IOException {
        var today = LocalDate.now();
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("+3m");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(today.plusMonths(3), parsedDateOrErr.getVal());
    }

    @Test
    public void testRelativeYears() throws IOException {
        var today = LocalDate.now();
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("+3y");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(today.plusYears(3), parsedDateOrErr.getVal());
    }

    @Test
    public void testInvalidStr() throws IOException {
        var parsedDateOrErr = ProjectionsDeserializer.parseRelativeDateStr("3y");
        Assert.assertTrue("Parsing of invalid string should have failed", parsedDateOrErr.hasGerr());
    }

    @Test
    public void testValidDeserialization() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFile.EXAMPLE);
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

        Assert.assertEquals(ExpectedProjectionsInfo.EXPECTED_SCENARIOS, projectionScenarios);
    }

    public void testInvalidYmlThrowsException() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = ExampleInputFile.ASSETS.getResource();
        AssetsWithHistory assetsWithHistory;
        assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = ProjectionsFile.INVALID_YML.getResource();
        Main.addDeserializersNeedingAssets(mapper, assetsWithHistory.getAssets());
        try {
            mapper.readValue(projectionsUrl, Projections.class);
            Assert.fail("Deserialization of projections should have failed due to invalid YAML but didn't");
        } catch (IOException e) {}
    }

    @Test
    public void testDeserializationWithNonexistentAssets() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();

        // Empty assets this time, so all projections will correspond to nonexistent assets
        var assetsWithHistory = ImmutableAssetsWithHistory.builder()
                .build();

        var projectionsUrl = ProjectionsFile.EXAMPLE.getResource();

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
        var projections = parseProjectionsFile(ProjectionsFile.PAST_DATE_IN_PROJECTION);

        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to a date in the past, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testNoErrorOnToday() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFile.CHANGE_ON_TODAY);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertFalse(
                "Expected scenario to pass parsing, but it failed",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorOnDependencyCycle() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFile.DEPENDENCY_CYLE);
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
        var projections = parseProjectionsFile(ProjectionsFile.TWO_CHNAGES_ON_SAME_DATE);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to two changes on the same date, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorWhenDependencyHasError() throws IOException {
        var projections = parseProjectionsFile(ProjectionsFile.DEPENDENCY_HAS_ERROR);
        var projectionScenariosOrErr = projections.getScenarios();
        for (String scenarioId : projectionScenariosOrErr.keySet()) {
            var scenarioOrErr = projectionScenariosOrErr.get(scenarioId);
            switch (scenarioId) {
                case ExpectedProjectionsInfo.SELL_HALF_BTC_1Y_ID:
                    Assert.assertTrue(
                            "Expected scenario '" + scenarioId + "' to fail parsing due to the intentionally-induced error, but it succeeded",
                            scenarioOrErr.hasGerr()
                    );
                    break;
                case ExpectedProjectionsInfo.SELL_OTHER_HALF_BTC_2Y_ID:
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
        var projections = parseProjectionsFile(ProjectionsFile.DEPEND_ON_NONEXISTENT_SCENARIO);
        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due depending on a nonexistent scenario, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    private static Projections parseProjectionsFile(ProjectionsFile testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = ExampleInputFile.ASSETS.getResource();
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);
        Main.addDeserializersNeedingAssets(mapper, assetsWithHistory.getAssets());

        var projectionsUrl = testFile.getResource();
        return mapper.readValue(projectionsUrl, Projections.class);
    }
}
