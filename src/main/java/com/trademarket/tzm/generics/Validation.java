package com.trademarket.tzm.generics;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.stereotype.Component;

import com.trademarket.tzm.user.validation.ValidationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class Validation<T> {

    private final Validator validator;

    public Validation(Validator validator) {
        this.validator = validator;
    }

    public void validateAll(T entity) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        violations.forEach(violation -> {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        if (!errors.isEmpty()) throw new ValidationException(errors);
    }

    @SuppressWarnings("unchecked")// to remove
    public void validate(Object entity, Map<String, Object> updates) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        updates.forEach((field, value) -> {
            try {
                Field entityField = entity.getClass().getDeclaredField(field);
                entityField.setAccessible(true);

                if (value instanceof Map && value != null) {
                    // Handle nested objects
                    Object nestedObject = entityField.getType().getDeclaredConstructor().newInstance();
                    try {
                        
                        validate(nestedObject, (Map<String, Object>) value); // Recursive validation
                    } catch (ValidationException nestedException) {
                        // Add nested errors to the main errors map
                        nestedException.getErrors().forEach((nestedField, nestedMessage) ->
                            errors.put(field + "." + nestedField, nestedMessage));
                    }
                    entityField.set(entity, nestedObject); // Assign the validated nested object
                } else if (entityField.getType().isEnum()) {
                    // Handle enum fields
                    @SuppressWarnings({ "rawtypes", "null" })//to remove
                    Object enumValue = Enum.valueOf((Class<Enum>) entityField.getType(), value.toString().toUpperCase());
                    entityField.set(entity, enumValue);
                } else {
                    // Assign simple fields directly
                    entityField.set(entity, value);
                }
                
            } catch (NoSuchFieldException e) {
                errors.put(field, "Invalid field: " + field);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                errors.put(field, "Unable to initialize field: " + field);
            } catch (IllegalArgumentException e) {
                errors.put(field, "Invalid value for field: " + field);
            }
        });
        // Validate each updated field
        updates.forEach((field, _) -> {
            System.out.println("to validate field: "+field);
            Set<ConstraintViolation<Object>> violations = validator.validateProperty(entity, field);
            violations.forEach(violation -> errors.put(field, violation.getMessage()));
            System.out.println("errors: "+errors);
        });

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
