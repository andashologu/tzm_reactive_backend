package com.trademarket.tzm.user.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trademarket.tzm.user.model.UserEntity;
import com.trademarket.tzm.user.service.UserService;
import com.trademarket.tzm.user.validation.ValidationException;

import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<Object>> createUser(@RequestBody UserEntity user) {
        return userService.saveUser(user)
            .map(_ -> ResponseEntity.status(HttpStatus.CREATED).body((Object) Map.of("message", "User created successfully")))
            .onErrorResume(ValidationException.class, ex -> {
                Map<String, String> errors = ex.getErrors();
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object) errors));
            })
            .onErrorResume(Exception.class, ex -> {
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) Map.of("error", "An unexpected error occurred", "details", ex.getMessage())));
            });
    }
}



