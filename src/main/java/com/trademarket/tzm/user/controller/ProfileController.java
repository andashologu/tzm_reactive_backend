package com.trademarket.tzm.user.controller;

import com.trademarket.tzm.generics.JsonConversion;
import com.trademarket.tzm.user.model.Address;
import com.trademarket.tzm.user.model.ProfileEntity;
import com.trademarket.tzm.user.service.ProfileService;
import com.trademarket.tzm.user.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/{user_id}")//during signup, even ef user cancel updating profile it will still be created with empty fields
    public Mono<ResponseEntity<ProfileEntity>> createProfile(@RequestBody ProfileEntity profileEntity, @PathVariable Long user_id) {
        profileEntity.setUserId(user_id);
        profileEntity.setAddress(JsonConversion.jsonToObject(JsonConversion.objectToJson(profileEntity.getAddress()), Address.class));
        return profileService.save(profileEntity)
                .map(savedProfile -> ResponseEntity.ok(savedProfile))
                .onErrorResume(_ -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PatchMapping("/{user_id}")//this done on settings
    public Mono<ResponseEntity<Object>> updateProfile(
        @PathVariable Long user_id,
        @RequestBody Map<String, Object> updates
    ) {
        return profileService.updateProfile(user_id, updates)
            .map(updatedProfile -> ResponseEntity.ok((Object) updatedProfile))
            .onErrorResume(ValidationException.class, ex -> 
                Mono.just(ResponseEntity.badRequest().body(ex.getErrors())))
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }
}