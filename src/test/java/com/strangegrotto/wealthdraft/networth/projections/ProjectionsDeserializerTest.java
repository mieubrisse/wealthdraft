package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.networth.Asset;
import com.strangegrotto.wealthdraft.networth.AssetsWithHistory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ProjectionsDeserializerTest {
    /*
    private interface ExpectedProjectionScenario extends ProjectionScenario {
        void validate();
    }

    private static class ExpectedSellAllBtc3yProperties implements ExpectedProjectionScenario {
        public static final String ID = "btc-3y";
        public static final String NAME = "Sell Bitcoin in 3 years for 15k";
        public static final Optional<String> BASE = Optional.empty();
        public static final int NUM_CHANGES = 2;

        @Override
        public String getName() { return "Sell Bitcoin in 3 years for 15k"; }

        @Override
        public Optional<String> getBase() { return Optional.empty(); }

        @Override
        public SortedMap<LocalDate, Map<String, AssetChange>> getAssetChanges() {
            var today = LocalDate.now();
            var expected = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                    today.plusYears(3), Map.of(
                            "btc", ImmutableBankAccountAssetChange.builder().
                    )
            ));
        }

        @Override
        public void validate() {

        }
    }

    private static class ExpectedSellHalfBtc1yProperties {
        public static final String ID = "half-btc-1y";
        public static final String NAME = "Sell Bitcoin in 3 years for 15k";
        public static final Optional<String> BASE = Optional.empty();
        public static final int NUM_CHANGES = 2;
    }

    private static class ExpectedSellOtherHalfBtc2yProperties {
        public static final String ID = "half-btc-2y";
        public static final String NAME = "Sell Bitcoin in 3 years for 15k";
        public static final Optional<String> BASE = Optional.empty();
        public static final int NUM_CHANGES = 2;
    }

    private static final Set<String> EXPECTED_SCENARIO_IDS = Sets.newHashSet(
            ExpectedSellAllBtc3yProperties.ID,
            ExpectedSellHalfBtc1yProperties.ID,
            ExpectedSellOtherHalfBtc2yProperties.ID
    );

    @Test
    public void testValidDeserialization() throws IOException {
        var mapper = Main.getObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        var assetsUrl = classLoader.getResource(TestYmlFile.ASSETS.getFilename());
        var assetsWithHistory = mapper.readValue(assetsUrl, AssetsWithHistory.class);

        var projectionsUrl = classLoader.getResource(TestYmlFile.PROJECTIONS.getFilename());

        addProjectionsDeserModule(mapper, assetsWithHistory.getAssets());
        var projections = mapper.readValue(projectionsUrl, Projections.class);

        var projectionScenarios = projections.getScenarios();
        Assert.assertEquals(EXPECTED_SCENARIO_IDS,  projectionScenarios.keySet());
        for (var scenarioId : projectionScenarios.keySet()) {
            var scenarioOrErr = projectionScenarios.get(scenarioId);
            Assert.assertFalse(
                    "Scenario " + scenarioId + " deserialized with an error when it should have deserialized successfully",
                    scenarioOrErr.hasGerr());
        }

        // NOTE: We won't test the parsing of the relative date strings like "+3y" because that's tested already
        //  in the test of the relative date deserializer

        var sellAllBtc3yScenario = projectionScenarios.get(ExpectedSellAllBtc3yProperties.ID).getVal();
        Assert.assertTrue(sellAllBtc3yScenario.getBase().isEmpty());
        var sellAllBtc3yChanges = sellAllBtc3yScenario.getAssetChanges();
        Assert.assertTrue(ExpectedSellAllBtc3yProperties.NUM_CHANGES, sellAllBtc3yChanges.size());
    }

    private static void addProjectionsDeserModule(ObjectMapper mapper, Map<String, Asset> assets) {
        var projectionsDeserializer = new ProjectionsDeserializer(assets);
        var projectionsDeserializationModule = new SimpleModule();
        projectionsDeserializationModule.addDeserializer(Projections.class, projectionsDeserializer);
        mapper.registerModule(projectionsDeserializationModule);
    }
     */
}
