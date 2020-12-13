package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.networth.Asset;
import com.strangegrotto.wealthdraft.networth.AssetsWithHistory;
import com.strangegrotto.wealthdraft.networth.ImmutableAsset;
import com.strangegrotto.wealthdraft.networth.ImmutableAssetsWithHistory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class ProjectionsDeserializerTest {

    @Test
    public void testValidDeserialization() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource(TestYmlFile.PROJECTIONS.getFilename());

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

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

    @Test
    public void testDeserializationWithNonexistentAssets() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();

        // Empty assets this time, so all projections will correspond to nonexistent assets
        var assetsWithHistory = ImmutableAssetsWithHistory.builder()
                .build();

        var projectionsUrl = classLoader.getResource(TestYmlFile.PROJECTIONS.getFilename());

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
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
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource("past-date-in-projection.yml");

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to a date in the past, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorOnDependencyCycle() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource("dependency-cycle.yml");

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

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
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource("two-changes-on-same-date.yml");

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due to two changes on the same date, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    @Test
    public void testErrorWhenDependencyHasError() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource("dependency-has-error.yml");

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

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
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource("depend-on-nonexistent-scenario.yml");

        addProjDeserializationModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

        var projectionScenariosOrErr = projections.getScenarios();
        var scenarioOrErr = projectionScenariosOrErr.get(ExpectedProjectionsInfo.SELL_ALL_BTC_3Y_ID);
        Assert.assertTrue(
                "Expected scenario to fail parsing due depending on a nonexistent scenario, but it succeeded",
                scenarioOrErr.hasGerr()
        );
    }

    private static void addProjDeserializationModule(ObjectMapper mapper, Map<String, Asset> assets) {
        var projectionsDeserializer = new ProjectionsDeserializer(assets);
        var projectionsDeserializationModule = new SimpleModule();
        projectionsDeserializationModule.addDeserializer(Projections.class, projectionsDeserializer);
        mapper.registerModule(projectionsDeserializationModule);
    }
}
