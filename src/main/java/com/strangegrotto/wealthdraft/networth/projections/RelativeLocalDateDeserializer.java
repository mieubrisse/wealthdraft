package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserializer for turning relative local date strings like "2020-10-22" and "+5m" into actual LocalDates
 */
public class RelativeLocalDateDeserializer extends JsonDeserializer<RelativeLocalDate> {

    // TODO Add support for "w" and "d"????
    private static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile("^\\+([0-9]+)([ym])$");

    @Override
    public RelativeLocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String rawStr = p.readValueAs(String.class);

        try {
            LocalDate asLocalDate = LocalDate.parse(rawStr);
            return ImmutableRelativeLocalDate.builder()
                    .localDate(asLocalDate)
                    .build();
        } catch (DateTimeParseException e) {}

        Matcher matcher = RELATIVE_DATE_PATTERN.matcher(rawStr);
        if (!matcher.find()) {
            throw new IOException("Relative date str '" + rawStr + "' does not match expected pattern " + RELATIVE_DATE_PATTERN.toString());
        }

        // Group 0 is the whole string, which is why we start with index 1
        long numberOfUnits = Long.parseLong(matcher.group(1));
        String units = matcher.group(2);

        LocalDate result = LocalDate.now();
        switch (units) {
            case "y":
                result = result.plusYears(numberOfUnits);
                break;
            case "m":
                result = result.plusMonths(numberOfUnits);
                break;
            default:
                throw new IOException("Unrecognized unit number '" + units + "'");
        }
        return ImmutableRelativeLocalDate.builder()
                .localDate(result)
                .build();
    }
}
