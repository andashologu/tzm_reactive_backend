package com.trademarket.tzm.user.controller;

import com.trademarket.tzm.generics.JsonConversion;
import com.trademarket.tzm.user.model.Address;
import com.trademarket.tzm.user.model.ProfileEntity;
import com.trademarket.tzm.user.repository.ProfileRepository;
import com.trademarket.tzm.user.repository.UserRepository;
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

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileController(ProfileService profileService, ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{user_id}") // During signup, even if the user cancels updating the profile, it will still be created with empty fields. this will be used only if user's profile was accidentally deleted or not created
    public Mono<ResponseEntity<Object>> createProfile(@RequestBody ProfileEntity profileEntity, @PathVariable Long user_id) {
        profileEntity.setUserId(user_id);
        profileEntity.setAddress(JsonConversion.jsonToObject(JsonConversion.objectToJson(profileEntity.getAddress()), Address.class));
        profileEntity.setCreatedAt(LocalDate.now());
        return userRepository.findById(user_id)
            .flatMap(_ -> 
                profileService.save(profileEntity)
                    .map(savedProfile -> ResponseEntity.ok((Object) savedProfile))
                    .onErrorResume(ValidationException.class, ex -> {
                        return Mono.just(ResponseEntity.badRequest().body(ex.getErrors()));
                    })
            )
            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")))
            .onErrorResume(Exception.class, ex -> {
                ex.printStackTrace();
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", ex.getMessage())));
            });
    }
 
    @PatchMapping("/{user_id}")
    public Mono<ResponseEntity<Object>> updateProfile(
        @PathVariable Long user_id,
        @RequestBody Map<String, Object> updates
    ) {
        return profileRepository.findIdByUserId(user_id)
            .flatMap(profileEntity -> 
                profileService.updateProfile(profileEntity, updates)
                    .map(updatedProfile -> ResponseEntity.ok((Object) updatedProfile))
            )
            .onErrorResume(ValidationException.class, ex -> {
                ex.printStackTrace();
                return Mono.just(ResponseEntity.badRequest().body(ex.getErrors()));
            }
            )
            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")));
    }

}