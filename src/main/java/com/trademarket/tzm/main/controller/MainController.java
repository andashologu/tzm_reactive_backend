package com.trademarket.tzm.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class MainController {

    @GetMapping("/")
    public Mono<String> main() {
        return Mono.just("Welcome to Home Page!");
    }
}
