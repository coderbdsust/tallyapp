package com.udayan.tallyapp.user.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.udayan.tallyapp.customexp.InvalidDateFormat;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class DateOfBirthDeserializer extends StdDeserializer<LocalDate> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    DateOfBirthDeserializer(){
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctx) throws IOException  {
        String value = parser.readValueAs(String.class);
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("{}",e);
            throw new InvalidDateFormat("Invalid date format. Expected format yyyy-MM-dd, Got " + value);
        }
    }
}
