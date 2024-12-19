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
        System.out.println("errors: "+errors);
        if (!errors.isEmpty()) throw new ValidationException(errors);
    }

    public void validate(Object entity, Map<String, Object> updates) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        updates.forEach((field, value) -> {
            try {
                Field entityField = entity.getClass().getDeclaredField(field);
                entityField.setAccessible(true);

                if (value instanceof Map && value != null) {
                    Object nestedObject = entityField.getType().getDeclaredConstructor().newInstance();
                    Map<String, Object> nestedMap = MapConverter.toNestedMap(value);
                    try {
                        
                        validate(nestedObject, nestedMap);
                    } catch (ValidationException nestedException) {
                        nestedException.getErrors().forEach((nestedField, nestedMessage) ->
                            errors.put(field + "." + nestedField, nestedMessage));
                    }
                    entityField.set(entity, nestedObject);
                } else if (entityField.getType().isEnum() && value != null) {
                    Object enumValue = EnumConversion.toEnum(entityField, value.toString());

                    entityField.set(entity, enumValue);
                
                } else {
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
        updates.forEach((field, _) -> {
            Set<ConstraintViolation<Object>> violations = validator.validateProperty(entity, field);
            violations.forEach(violation -> errors.put(field, violation.getMessage()));
        });

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
