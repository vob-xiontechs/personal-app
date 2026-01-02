package com.dev.backendapi.service.profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.backendapi.entity.profile.UserEntity;
import com.dev.backendapi.exception.BusinessException;
import com.dev.backendapi.io.profile.ProfileRequest;
import com.dev.backendapi.io.profile.ProfileResponse;
import com.dev.backendapi.repository.profile.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileValidator profileValidator;
    private final ProfileMapper profileMapper;
    private final ProfileDomainService profileDomainService;

    @Override
    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        // Validate business rules
        profileValidator.validateCreateProfileRequest(request);

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS");
        }

        // Create user entity with domain logic
        UserEntity newProfile = profileDomainService.createNewProfile(request);

        // Save to repository
        newProfile = userRepository.save(newProfile);

        // Convert to response
        return profileMapper.toProfileResponse(newProfile);
    }
}
