package com.trademarket.tzm.generics;

import java.util.HashMap;
import java.util.Map;

public class MapConverter {

    /**
     * Converts an object to a Map<String, Object> by validating and rebuilding it.
     *
     * @param obj the object to be converted
     * @return a new Map<String, Object> with validated entries
     * @throws IllegalArgumentException if the object cannot be converted
     */
    public static Map<String, Object> toNestedMap(Object obj) {
        if (!(obj instanceof Map)) throw new IllegalArgumentException("Object is not a Map");

        Map<?, ?> originalMap = (Map<?, ?>) obj;
        Map<String, Object> validatedMap = new HashMap<>();

        for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
            if (!(entry.getKey() instanceof String)) throw new IllegalArgumentException("Map key is not a String: " + entry.getKey());
            validatedMap.put((String) entry.getKey(), entry.getValue());
        }

        return validatedMap;
    }
}
