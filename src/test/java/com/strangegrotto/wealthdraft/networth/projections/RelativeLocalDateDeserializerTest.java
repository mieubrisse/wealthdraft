package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

public class RelativeLocalDateDeserializerTest {

    @Test
    public void testPinnedDate() throws IOException {
        var mapper = Main.getObjectMapper();
        var parsedDate = mapper.readValue("2020-10-31", RelativeLocalDate.class);
        Assert.assertEquals(LocalDate.of(2020, 10, 31), parsedDate.getLocalDate());
    }

    @Test
    public void testRelativeMonths() throws IOException {
        var today = LocalDate.now();
        var mapper = Main.getObjectMapper();
        var parsedDate = mapper.readValue("+3m", RelativeLocalDate.class);
        Assert.assertEquals(today.plusMonths(3), parsedDate.getLocalDate());
    }

    @Test
    public void testRelativeYears() throws IOException {
        var today = LocalDate.now();
        var mapper = Main.getObjectMapper();
        var parsedDate = mapper.readValue("+3y", RelativeLocalDate.class);
        Assert.assertEquals(today.plusYears(3), parsedDate.getLocalDate());
    }

    @Test(expected = IOException.class)
    public void testInvalidStr() throws IOException {
        var mapper = Main.getObjectMapper();
        mapper.readValue("3y", RelativeLocalDate.class);
    }
}
