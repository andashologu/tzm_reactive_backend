package com.trademarket.tzm.generics;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Map;

@Component
public class Repository<T> {

    private final DatabaseClient databaseClient;

    public Repository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<T> updateFields(Long id, Map<String, Object> fieldsToUpdate, Class<T> entityClass, T entity) {
        if (fieldsToUpdate.isEmpty()) return Mono.error(new IllegalArgumentException("No fields to update"));

        String tableName = getTableName(entityClass);
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

        fieldsToUpdate.forEach((key, _) -> sql.append(key).append(" = :").append(key).append(", "));

        sql.setLength(sql.length() - 2);

        sql.append(" WHERE id = :id");

        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString()).bind("id", id);
        Class<?> entityObj = entity.getClass();
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
            try {
                Field entityField = entityObj.getDeclaredField(entry.getKey());
                entityField.setAccessible(true);
                entityField.set(entity, entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return Mono.error(new RuntimeException("Failed to update entity field: " + entry.getKey(), e));
            }
        }

        return spec.fetch().rowsUpdated()
            .flatMap(rowsUpdated -> {
                if (rowsUpdated == 0) return Mono.error(new IllegalArgumentException("Entity not found with id: " + id));
                return Mono.just(entity);
            });
    }

    private String getTableName(Class<T> entityClass) {
        if (entityClass.isAnnotationPresent(org.springframework.data.relational.core.mapping.Table.class))
            return entityClass.getAnnotation(org.springframework.data.relational.core.mapping.Table.class).value();
        throw new IllegalArgumentException("Entity class " + entityClass.getSimpleName() + " must have @Table annotation");
    }
}
