package com.trademarket.tzm.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.trademarket.tzm.user.model.UserEntity;
import com.trademarket.tzm.user.repository.UserRepository;
import com.trademarket.tzm.user.service.UserService;
import com.trademarket.tzm.user.validation.ValidationException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

/*
    @GetMapping
    public Flux<UserEntity> searchUsers(
        @RequestParam String search,
        @RequestParam(required = false) Boolean active
    ) {
        return userService.searchUsers(search, active);
    }
*/
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(user -> ResponseEntity.ok((Object) user))
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) "User not found"));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Object>> updateUser(
        @PathVariable Long id,
        @RequestBody Map<String, Object> updates
    ) {
        return userService.updateUser(id, updates)
            .map(updatedUser -> ResponseEntity.ok((Object) updatedUser))
            .onErrorResume(ValidationException.class, ex -> 
                Mono.just(ResponseEntity.badRequest().body(ex.getErrors())))
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

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

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
            .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()))
            .onErrorResume(_ -> {
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred"));
            });
    }
}
