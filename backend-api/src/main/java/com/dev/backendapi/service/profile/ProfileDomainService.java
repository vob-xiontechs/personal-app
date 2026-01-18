package com.dev.backendapi.service.profile;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dev.backendapi.constants.profile.ProfileConstants;
import com.dev.backendapi.entity.profile.UserEntity;
import com.dev.backendapi.io.profile.ProfileRequest;

@Service
public class ProfileDomainService {

    public UserEntity createNewProfile(ProfileRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpireAt(ProfileConstants.DEFAULT_OTP_EXPIRE_TIME)
                .resetOtp(null)
                .resetOtpExpireAt(ProfileConstants.DEFAULT_OTP_EXPIRE_TIME)
                .build();
    }

    public boolean isValidProfile(UserEntity userEntity) {
        return userEntity != null &&
               userEntity.getUserId() != null &&
               userEntity.getEmail() != null &&
               userEntity.getName() != null &&
               userEntity.getPassword() != null;
    }

    public void prepareForVerification(UserEntity userEntity, String otp, long expireTime) {
        userEntity.setVerifyOtp(otp);
        userEntity.setVerifyOtpExpireAt(expireTime);
        userEntity.setIsAccountVerified(false);
    }

    public void markAsVerified(UserEntity userEntity) {
        userEntity.setIsAccountVerified(true);
        userEntity.setVerifyOtp(null);
        userEntity.setVerifyOtpExpireAt(0L);
    }

    public UserEntity updateProfile(UserEntity userEntity, ProfileRequest request) {
        // Update the user entity with new values
        userEntity.setName(request.getName().trim());
        userEntity.setEmail(request.getEmail().toLowerCase().trim());
        userEntity.setPassword(request.getPassword());

        // Note: We don't update verification status during regular profile updates
        // That should be handled separately through verification process

        return userEntity;
    }
}
