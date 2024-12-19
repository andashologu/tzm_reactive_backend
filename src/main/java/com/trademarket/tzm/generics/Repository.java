package com.trademarket.tzm.generics;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import io.r2dbc.postgresql.codec.Json;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class Repository<T> {

    private final DatabaseClient databaseClient;

    public Repository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Map<String, Object>> updateFields(Long id, Map<String, Object> fieldsToUpdate, Class<T> entityClass, T entity) {
        if (fieldsToUpdate.isEmpty()) return Mono.error(new IllegalArgumentException("No fields to update"));
        
    
        String tableName = getTableName(entityClass);
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
    
        fieldsToUpdate.forEach((key, _) -> sql.append(toSnakeCase(key)).append(" = :").append(key).append(", "));
        sql.setLength(sql.length() - 2);
    
        sql.append(" WHERE id = :id");
    
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString()).bind("id", id);
    
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
    
            if (value instanceof Map) {
                Json serializedValue = JsonConversion.objectToJson(value);
                spec = spec.bind(key, serializedValue);
            } else {
                spec = spec.bind(key, value);
            }
        }
    
        return spec.fetch().rowsUpdated()
            .flatMap(rowsUpdated -> {
                if (rowsUpdated == 0) {
                    return Mono.error(new IllegalArgumentException("Entity not found with id: " + id));
                }
                return Mono.just(fieldsToUpdate);
            });
    }
    
    private String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
    

    private String getTableName(Class<T> entityClass) {
        if (entityClass.isAnnotationPresent(org.springframework.data.relational.core.mapping.Table.class))
            return entityClass.getAnnotation(org.springframework.data.relational.core.mapping.Table.class).value();
        throw new IllegalArgumentException("Entity class " + entityClass.getSimpleName() + " must have @Table annotation");
    }
}
