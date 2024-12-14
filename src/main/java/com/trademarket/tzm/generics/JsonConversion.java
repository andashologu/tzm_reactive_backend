package com.trademarket.tzm.generics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;

public class JsonConversion {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts an object to JSON for storage.
     *
     * @param object The object to convert.
     * @param <T>    The type of the object.
     * @return A Json object suitable for database storage.
     */
    public static <T> Json objectToJson(T object) {
        try {
            return Json.of(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Converts a JSON field to an object of the specified type.
     *
     * @param json       The Json object to convert.
     * @param targetType The target class type.
     * @param <T>        The type of the target object.
     * @return The deserialized object.
     */
    public static <T> T jsonToObject(Json json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json.asString(), targetType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }

    /**
     * Converts a JSON field to an object using a generic type reference.
     * Useful for collections or maps.
     *
     * @param json           The Json object to convert.
     * @param typeReference  The target type reference.
     * @param <T>            The type of the target object.
     * @return The deserialized object.
     */
    public static <T> T jsonToObject(Json json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json.asString(), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to object with type reference", e);
        }
    }
}


