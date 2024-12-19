package com.trademarket.tzm.generics;

import java.lang.reflect.Field;

public class EnumConversion {

    /**
     * Converts a string to an Enum constant dynamically without casting.
     *
     * @param type  the class of the Enum
     * @param value the string value to convert
     * @return the Enum constant corresponding to the string value
     * @throws IllegalArgumentException if the type is not an Enum or if the value is invalid
     */
    public static Enum<?> toEnum(Class<?> type, String value) {
        if (!type.isEnum()) throw new IllegalArgumentException("Provided type is not an Enum: " + type.getName());
     
        for (Object constant : type.getEnumConstants()) {
            Enum<?> enumConstant = (Enum<?>) constant;
            if (enumConstant.name().equalsIgnoreCase(value)) {
                return enumConstant;
            }
        }

        throw new IllegalArgumentException(
                "No enum constant " + type.getName() + "." + value.toUpperCase()
        );
    }

    /**
     * Converts a string to an Enum constant using a Field object.
     *
     * @param entityField the Field object representing the enum field
     * @param value       the string value to convert
     * @return the Enum constant corresponding to the string value
     * @throws IllegalArgumentException if the field's type is not an Enum or if the value is invalid
     */
    public static Object toEnum(Field entityField, String value) {
        Class<?> fieldType = entityField.getType();
        return toEnum(fieldType, value);
    }
}
