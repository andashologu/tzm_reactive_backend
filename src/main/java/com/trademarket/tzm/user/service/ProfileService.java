package com.trademarket.tzm.user.service;

import com.trademarket.tzm.generics.JsonConversion;
import com.trademarket.tzm.generics.Repository;
import com.trademarket.tzm.generics.Validation;
import com.trademarket.tzm.user.model.Address;
import com.trademarket.tzm.user.model.Preferences;
import com.trademarket.tzm.user.model.ProfileEntity;
import com.trademarket.tzm.user.model.Settings;
import com.trademarket.tzm.user.repository.ProfileRepository;
import com.trademarket.tzm.user.validation.ValidationException;

import io.r2dbc.postgresql.codec.Json;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.Map;

@Service
public class ProfileService {

    private final DatabaseClient databaseClient;
    private final ProfileRepository profileRepository;
    private final Validation<ProfileEntity> customValidation;
    private final Repository<ProfileEntity> customRepository;

    public ProfileService(DatabaseClient databaseClient, ProfileRepository profileRepository, Validation<ProfileEntity> customValidation, Repository<ProfileEntity> customRepository) {
        this.databaseClient = databaseClient;
        this.profileRepository = profileRepository;
        this.customValidation = customValidation;
        this.customRepository = customRepository;
    }

    public Mono<ProfileEntity> save(ProfileEntity profile) {
        return Mono.fromCallable(() -> {
                customValidation.validateAll(profile);
                return profile;
            }).subscribeOn(Schedulers.boundedElastic())
            .then(databaseClient.sql("""
                    INSERT INTO profiles (
                        user_id, firstname, lastname, profile_picture, bio, phone, website, address, preferences, settings, created_at, updated_at
                    ) VALUES (
                        :user_id, :firstname, :lastname, :profile_picture, :bio, :phone, :website, :address, :preferences, :settings, :created_at, :updated_at
                    )
                    RETURNING id, user_id, firstname, lastname, profile_picture, bio, phone, website, address, preferences, settings, created_at, updated_at
                """)
                .bind("user_id", profile.getUserId())
                .bind("firstname", profile.getFirstname())
                .bind("lastname", profile.getLastname())
                .bind("profile_picture", profile.getProfilePicture())
                .bind("bio", profile.getBio())
                .bind("phone", profile.getPhone())
                .bind("website", profile.getWebsite())
                .bind("address", JsonConversion.objectToJson(profile.getAddress()))
                .bind("preferences", JsonConversion.objectToJson(profile.getPreferences()))
                .bind("settings", JsonConversion.objectToJson(profile.getSettings()))
                .bind("created_at", profile.getCreatedAt())
                .bind("updated_at", profile.getUpdatedAt())
                .map((row, _) -> {
                    ProfileEntity savedProfileEntity = new ProfileEntity(
                        row.get("id", Long.class),
                        row.get("user_id", Long.class),
                        row.get("firstname", String.class),
                        row.get("lastname", String.class),
                        row.get("profile_picture", String.class),
                        row.get("bio", String.class),
                        row.get("phone", String.class),
                        row.get("website", String.class),
                        JsonConversion.jsonToObject(row.get("address", Json.class), Address.class),
                        JsonConversion.jsonToObject(row.get("preferences", Json.class), Preferences.class),
                        JsonConversion.jsonToObject(row.get("settings", Json.class), Settings.class)
                    );
                    savedProfileEntity.setCreatedAt(row.get("created_at", LocalDate.class));
                    savedProfileEntity.setUpdatedAt(row.get("updated_at", LocalDate.class));
                    return savedProfileEntity;
                })
                .one()
            )   
            .onErrorResume(error -> {
                System.err.println("Error occurred while saving profile: " + error.getMessage());
                error.printStackTrace();
                return Mono.error(new RuntimeException("Failed to save profile", error));
            });
    }

    public Mono<ProfileEntity> updateProfile(Long user_id, Map<String, Object> updates) {
        return profileRepository.findUserIdByUserId(user_id)
            .switchIfEmpty(Mono.error(new ValidationException(Map.of("id", "Profile not found"))))
            .flatMap(existingProfile -> 
                Mono.fromCallable(() -> {
                    customValidation.validate(new ProfileEntity(), updates);
                    return existingProfile;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(_ -> customRepository.updateFields(user_id, updates, ProfileEntity.class, existingProfile))
            );
    }
}