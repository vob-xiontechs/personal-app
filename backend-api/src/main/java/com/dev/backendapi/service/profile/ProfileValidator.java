package com.dev.backendapi.service.profile;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dev.backendapi.constants.profile.ProfileConstants;
import com.dev.backendapi.exception.ValidationException;
import com.dev.backendapi.io.profile.ProfileRequest;

@Component
public class ProfileValidator {

    public void validateCreateProfileRequest(ProfileRequest request) {
        if (request == null) {
            throw new ValidationException("Profile request cannot be null");
        }

        validateName(request.getName());
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException(ProfileConstants.NAME_REQUIRED);
        }

        if (name.trim().length() > ProfileConstants.MAX_NAME_LENGTH) {
            throw new ValidationException(ProfileConstants.NAME_TOO_LONG);
        }

        if (!name.matches(ProfileConstants.NAME_PATTERN)) {
            throw new ValidationException(ProfileConstants.INVALID_NAME_FORMAT);
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException(ProfileConstants.EMAIL_REQUIRED);
        }

        if (email.length() > ProfileConstants.MAX_EMAIL_LENGTH) {
            throw new ValidationException(ProfileConstants.EMAIL_TOO_LONG);
        }

        if (!email.matches(ProfileConstants.EMAIL_PATTERN)) {
            throw new ValidationException(ProfileConstants.INVALID_EMAIL_FORMAT);
        }
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new ValidationException(ProfileConstants.PASSWORD_REQUIRED);
        }

        if (password.length() < ProfileConstants.MIN_PASSWORD_LENGTH) {
            throw new ValidationException(ProfileConstants.PASSWORD_TOO_SHORT);
        }

        // Check for at least one uppercase, one lowercase, and one digit
        if (!password.matches(ProfileConstants.PASSWORD_PATTERN)) {
            throw new ValidationException(ProfileConstants.PASSWORD_WEAK);
        }
    }
}
