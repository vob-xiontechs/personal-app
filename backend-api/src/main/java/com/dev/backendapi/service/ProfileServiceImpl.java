package com.dev.backendapi.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.backendapi.config.ErrorMessageConfig;
import com.dev.backendapi.entity.UserEntity;
import com.dev.backendapi.exception.BusinessException;
import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    private final UserRepository userRepository;
    private final ErrorMessageConfig errorMessageConfig;

    @Override
    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("EMAIL_EXISTS");
        }

        UserEntity newProfile = convertToUserEntity(request);

        newProfile = userRepository.save(newProfile);

        return convertToProfileResponse(newProfile);
    }


    private ProfileResponse convertToProfileResponse(UserEntity newProfile){
        return new ProfileResponse(
            newProfile.getUserId(),
            newProfile.getName(),
            newProfile.getEmail(),
            newProfile.getIsAccountVerified()
        );
    }
    private UserEntity convertToUserEntity(ProfileRequest request){
        return UserEntity.builder()
                        .email(request.getEmail())
                        .userId(UUID.randomUUID().toString())
                        .name(request.getName())
                        .password(request.getPassword())
                        .isAccountVerified(false)
                        .resetOtpExpireAt(0L)
                        .verifyOtp(null)
                        .verifyOtpExpireAt(0L)
                        .resetOtp(null)
                        .build();

               
    }

    
}
