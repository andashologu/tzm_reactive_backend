package com.trademarket.tzm.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import com.trademarket.tzm.user.repository.UserRepository;

@Component
public class UniqueEmailConstraint implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    public UniqueEmailConstraint(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return true;

        return !userRepository.findByEmail(email)
            .hasElement()
            .blockOptional()
            .orElse(false);
    }
}

