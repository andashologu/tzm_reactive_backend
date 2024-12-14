package com.trademarket.tzm.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

    @GetMapping("/greet")
    public Mono<String> greet(@RequestParam String name) {
        return Mono.just("Hello " + name);
    }
}
