package com.tourmate.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;


/**
 * JPA AttributeConverter for persisting a {@code List<String>} as a JSON string.
 * Uses Jackson {@link ObjectMapper} to serialize/deserialize the list.
 * Applied automatically by JPA because of the {@link Converter} annotation.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

     // ObjectMapper is thread-safe after configuration, so one instance is enough.
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts a Java List<String> into a JSON string for storing in the database.
     *
     * @param attribute the list to convert
     * @return JSON string representation of the list
     * @throws RuntimeException if serialization fails
     */
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing list", e);
        }
    }

    /**
     * Converts a JSON string from the database back into a Java List<String>.
     *
     * @param dbData JSON string from the database
     * @return deserialized list of strings
     * @throws RuntimeException if deserialization fails
     */
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing list", e);
        }
    }
}
