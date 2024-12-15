package com.trademarket.tzm.user.service;

import com.trademarket.tzm.generics.Repository;
import com.trademarket.tzm.generics.Validation;
import com.trademarket.tzm.user.model.UserEntity;
import com.trademarket.tzm.user.repository.UserRepository;
import com.trademarket.tzm.user.validation.ValidationException;

import java.util.Map;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserService {

    private final DatabaseClient databaseClient;
    private final UserRepository userRepository;
    private final Validation<UserEntity> customValidation;
    private final Repository<UserEntity> customRepository;

    public UserService(DatabaseClient databaseClient, UserRepository userRepository, Validation<UserEntity> customValidation, Repository<UserEntity> customRepository) {
        this.databaseClient = databaseClient;
        this.userRepository = userRepository;
        this.customValidation = customValidation;
        this.customRepository = customRepository;
    }

    public Flux<UserEntity> searchUsers(String query, Boolean active) { //search fielder will need to be reimplemented since firstname and lastname are moved to a different entity
        StringBuilder sql = new StringBuilder("""
            SELECT *, similarity(username, :query) AS score
            FROM users
            WHERE (
                username % :query OR
                email % :query
            )
            ORDER BY score DESC
        """);

        if (active != null) sql.append(" AND active = :active");
        
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql.toString())
            .bind("query", query);

        if (active != null) spec = spec.bind("active", active);
        
        return spec.map((row, _) -> new UserEntity(
            row.get("id", Long.class),
            row.get("username", String.class),
            row.get("email", String.class),
            row.get("active", Boolean.class)
        )).all();
    }

    public Mono<UserEntity> findByUsernameOrEmail(String identifier) {
        String sql = """
            SELECT *
            FROM users
            WHERE username = :identifier OR email = :identifier
        """;

        return databaseClient.sql(sql)
            .bind("identifier", identifier)
            .map((row, _) -> new UserEntity(
                row.get("id", Long.class),
                row.get("username", String.class),
                row.get("email", String.class),
                row.get("active", Boolean.class)
            )).one();
    }

    public Mono<UserEntity> saveUser(UserEntity user) {
        return Mono.fromCallable(() -> {
                customValidation.validateAll(user);
                return user;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(userRepository::save);
    }
    
    public Mono<Object> updateUser(Long id, Map<String, Object> updates) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new ValidationException(Map.of("id", "User not found"))))
            .flatMap(existingUser -> 
                Mono.fromCallable(() -> {
                    customValidation.validate(new UserEntity(), updates);
                    return existingUser;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(_ -> customRepository.updateFields(id, updates, UserEntity.class, existingUser))
            );
    }
    
    public Mono<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
