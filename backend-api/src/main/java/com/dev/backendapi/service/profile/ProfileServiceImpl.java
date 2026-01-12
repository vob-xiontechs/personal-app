package com.dev.backendapi.service.profile;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.backendapi.controller.dto.PaginatedResponse;
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

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponse> getProfileList() {
        // Get all users from repository
        List<UserEntity> userEntities = userRepository.findAll();

        // Convert to response objects
        return userEntities.stream()
                .map(profileMapper::toProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileDetails(String userId) {
        // Find user by userId (business key)
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Convert to response object
        return profileMapper.toProfileResponse(userEntity);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(String userId, ProfileRequest request) {
        // Find user by userId (business key)
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Update user details
        UserEntity updatedUser = profileDomainService.updateProfile(userEntity, request);

        // Save to repository
        updatedUser = userRepository.save(updatedUser);

        // Convert to response
        return profileMapper.toProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteProfile(String userId) {
        // Find user by userId (business key)
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Hard delete the user from database
        userRepository.delete(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProfileResponse> getProfileList(int page, int size, String sortBy, String sortDirection) {
        // Create sorting
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get paginated results
        Page<UserEntity> userPage = userRepository.findAll(pageable);

        // Convert to response objects
        List<ProfileResponse> content = userPage.getContent().stream()
                .map(profileMapper::toProfileResponse)
                .collect(Collectors.toList());

        // Build paginated response
        return PaginatedResponse.<ProfileResponse>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .empty(userPage.isEmpty())
                .build();
    }
}
