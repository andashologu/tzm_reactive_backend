package com.trademarket.tzm.user.repository;

import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.trademarket.tzm.user.model.ProfileEntity;

public interface ProfileRepository extends R2dbcRepository<ProfileEntity, Long> {

    @Query("SELECT user_id FROM profiles WHERE user_id = :userid")
    Mono<ProfileEntity> findUserIdByUserId(Long userId);

    @Query("SELECT id FROM profiles WHERE user_id = :userid")
    Mono<ProfileEntity> findIdByUserId(Long userId);
}
