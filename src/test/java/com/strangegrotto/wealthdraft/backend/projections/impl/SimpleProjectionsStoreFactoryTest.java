package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assets.AssetDefinitionsTestFiles;
import com.strangegrotto.wealthdraft.backend.assets.api.MockAssetStore;
import com.strangegrotto.wealthdraft.backend.assets.impl.SimpleAssetsStoreFactoryTest;
import com.strangegrotto.wealthdraft.backend.projections.ProjectionsTestFiles;
import com.strangegrotto.wealthdraft.backend.projections.api.ProjectionsStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleProjectionsStoreFactoryTest {
    @Test
    public void testPinnedDate() {
        var parsedDateOrErr = SerProjectionsDeserializer.parseRelativeDateStr("2020-10-31");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(LocalDate.of(2020, 10, 31), parsedDateOrErr.getVal());
    }

    @Test
    public void testRelativeMonths() {
        var today = LocalDate.now();
        var parsedDateOrErr = SerProjectionsDeserializer.parseRelativeDateStr("+3m");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(today.plusMonths(3), parsedDateOrErr.getVal());
    }

    @Test
    public void testRelativeYears() {
        var today = LocalDate.now();
        var parsedDateOrErr = SerProjectionsDeserializer.parseRelativeDateStr("+3y");
        Assert.assertFalse("Relative date parsing should not have thrown an error", parsedDateOrErr.hasGerr());
        Assert.assertEquals(today.plusYears(3), parsedDateOrErr.getVal());
    }

    @Test
    public void testInvalidStr() {
        var parsedDateOrErr = SerProjectionsDeserializer.parseRelativeDateStr("3y");
        Assert.assertTrue("Parsing of invalid string should have failed", parsedDateOrErr.hasGerr());
    }

    @Test
    public void testValidDeserialization() throws IOException {
        var projectionsStore = parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.EXAMPLE);
        var projectionScenarios = projectionsStore.getScenarios();
        Assert.assertEquals(ExpectedExampleProjections.EXPECTED_SCENARIOS, projectionScenarios);
    }

    @Test(expected = IOException.class)
    public void testInvalidYmlThrowsException() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.INVALID_YML);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeserializationWithNonexistentAssets() throws IOException {
        var emptyAssetsStore = new MockAssetStore(Map.of());
        var mapper = Main.getObjectMapper();
        var factory = new SimpleProjectionsStoreFactory(mapper, emptyAssetsStore);
        factory.create(ProjectionsTestFiles.EXAMPLE.getResource());
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnPastDate() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.PAST_DATE_IN_PROJECTION);
    }

    @Test
    public void testNoErrorOnToday() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.CHANGE_ON_TODAY);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnDependencyCycle() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.DEPENDENCY_CYLE);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnTwoChangesOnSameDate() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.TWO_CHNAGES_ON_SAME_DATE);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnTwoChangesOnSameDateFromDiffScenarios() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.TWO_CHANGES_ON_SAME_DATE_FROM_DIFF_SCENARIOS);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorWhenDependencyHasError() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.DEPENDENCY_HAS_ERROR);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorWhenDependingOnNonexistentScenario() throws IOException {
        parseProjectionsFile(Main.getObjectMapper(), ProjectionsTestFiles.DEPEND_ON_NONEXISTENT_SCENARIO);
    }

    private static ProjectionsStore parseProjectionsFile(ObjectMapper mapper, ProjectionsTestFiles testFile) throws IOException {
        var assetsStore = SimpleAssetsStoreFactoryTest.parseAssetsFile(mapper, AssetDefinitionsTestFiles.EXAMPLE);
        var factory = new SimpleProjectionsStoreFactory(mapper, assetsStore);
        return factory.create(testFile.getResource());
    }
}
