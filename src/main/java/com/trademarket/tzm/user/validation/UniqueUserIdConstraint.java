package com.trademarket.tzm.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import com.trademarket.tzm.user.repository.ProfileRepository;

@Component
public class UniqueUserIdConstraint implements ConstraintValidator<UniqueUserId, Long> {

    private final ProfileRepository profileRepository;

    public UniqueUserIdConstraint(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        if (userId == null) return true;
        
        return !profileRepository.findUserIdByUserId(userId)
                                .hasElement()
                                .blockOptional()
                                .orElse(false);
    }

}
