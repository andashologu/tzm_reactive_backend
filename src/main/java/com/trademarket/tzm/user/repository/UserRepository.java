package com.trademarket.tzm.user.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.trademarket.tzm.user.model.UserEntity;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    Mono<UserEntity> findByUsername(String username);
    Mono<UserEntity> findByEmail(String email);

    @Query("SELECT * FROM users WHERE username = :username OR email = :email")
    Flux<UserEntity> findByUsernameOrEmail(String username, String email);
}