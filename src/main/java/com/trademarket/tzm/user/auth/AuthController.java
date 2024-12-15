package com.trademarket.tzm.user.auth;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trademarket.tzm.user.model.ProfileEntity;
import com.trademarket.tzm.user.model.UserEntity;
import com.trademarket.tzm.user.repository.ProfileRepository;
import com.trademarket.tzm.user.service.UserService;
import com.trademarket.tzm.user.validation.ValidationException;

import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    private final UserService userService;
    private final ProfileRepository profileRepository;

    public AuthController(UserService userService, ProfileRepository profileRepository) {
        this.userService = userService;
        this.profileRepository = profileRepository;
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<Object>> createUser(@RequestBody UserEntity user) {
        return userService.saveUser(user)
            .flatMap(savedUser -> {
                // Create a new profile with only user_id set
                ProfileEntity profileEntity = new ProfileEntity();
                profileEntity.setUserId(savedUser.getId());
                profileEntity.setCreatedAt(LocalDate.now());//we want the date to match with when user was signing up
                
                return profileRepository.save(profileEntity)//no validation like address etc required profile has to be created
                    .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                        .body((Object) Map.of("message", "User created successfully"))));
            })
            .onErrorResume(ValidationException.class, ex -> {
                Map<String, String> errors = ex.getErrors();
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object) errors));
            })
            .onErrorResume(Exception.class, ex -> {
                ex.printStackTrace(); // Log the exception for debugging
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body((Object) Map.of("error", "An unexpected error occurred", "details", ex.getMessage())));
            });
    }

}



