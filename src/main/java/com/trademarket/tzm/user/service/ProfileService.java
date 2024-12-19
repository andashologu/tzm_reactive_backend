package com.trademarket.tzm.user.service;

import com.trademarket.tzm.generics.JsonConversion;
import com.trademarket.tzm.generics.Repository;
import com.trademarket.tzm.generics.Validation;
import com.trademarket.tzm.user.model.Address;
import com.trademarket.tzm.user.model.Preferences;
import com.trademarket.tzm.user.model.ProfileEntity;
import com.trademarket.tzm.user.model.Settings;

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
    private final Validation<ProfileEntity> customValidation;
    private final Repository<ProfileEntity> customRepository;

    public ProfileService(DatabaseClient databaseClient, Validation<ProfileEntity> customValidation, Repository<ProfileEntity> customRepository) {
        this.databaseClient = databaseClient;
        this.customValidation = customValidation;
        this.customRepository = customRepository;
    }

/*
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
*/
    
    public Mono<ProfileEntity> save(ProfileEntity profile) {
        return Mono.fromCallable(() -> {
                customValidation.validateAll(profile);
                return profile;
            }).subscribeOn(Schedulers.boundedElastic())
            .then(
                bindValuesToSpec(databaseClient.sql("""
                        INSERT INTO profiles (
                            user_id, firstname, lastname, profile_picture, bio, phone, website, address, preferences, settings, created_at, updated_at
                        ) VALUES (
                            :user_id, :firstname, :lastname, :profile_picture, :bio, :phone, :website, :address, :preferences, :settings, :created_at, :updated_at
                        )
                        RETURNING id, user_id, firstname, lastname, profile_picture, bio, phone, website, address, preferences, settings, created_at, updated_at
                    """), profile)
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
            );
    }

    private DatabaseClient.GenericExecuteSpec bindValuesToSpec(DatabaseClient.GenericExecuteSpec spec, ProfileEntity profile) {
        spec = bindOrNull(spec, "user_id", profile.getUserId(), Long.class);
        spec = bindOrNull(spec, "firstname", profile.getFirstname(), String.class);
        spec = bindOrNull(spec, "lastname", profile.getLastname(), String.class);
        spec = bindOrNull(spec, "profile_picture", profile.getProfilePicture(), String.class);
        spec = bindOrNull(spec, "bio", profile.getBio(), String.class);
        spec = bindOrNull(spec, "phone", profile.getPhone(), String.class);
        spec = bindOrNull(spec, "website", profile.getWebsite(), String.class);
        spec = bindOrNull(spec, "address", JsonConversion.objectToJson(profile.getAddress()), Json.class);
        spec = bindOrNull(spec, "preferences", JsonConversion.objectToJson(profile.getPreferences()), Json.class);
        spec = bindOrNull(spec, "settings", JsonConversion.objectToJson(profile.getSettings()), Json.class);
        spec = bindOrNull(spec, "created_at", profile.getCreatedAt(), LocalDate.class);
        spec = bindOrNull(spec, "updated_at", profile.getUpdatedAt(), LocalDate.class);
        return spec;
    }
    private <T> DatabaseClient.GenericExecuteSpec bindOrNull(DatabaseClient.GenericExecuteSpec spec, String key, T value, Class<T> type) {
        return value != null ? spec.bind(key, value) : spec.bindNull(key, type);
    }

    public Mono<Object> updateProfile(ProfileEntity profileEntity, Map<String, Object> updates) {
        return Mono.fromCallable(() -> {
                customValidation.validate(profileEntity, updates);
                return profileEntity;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(validatedProfile -> {
                Long id = validatedProfile.getId(); 
                if (id == null) return Mono.error(new IllegalArgumentException("Profile ID cannot be null for update"));
                return customRepository.updateFields(id, updates, ProfileEntity.class, validatedProfile);
            });
    }
    
}