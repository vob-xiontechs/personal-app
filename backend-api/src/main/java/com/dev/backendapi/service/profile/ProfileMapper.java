package com.dev.backendapi.service.profile;

import org.springframework.stereotype.Component;

import com.dev.backendapi.entity.profile.UserEntity;
import com.dev.backendapi.io.profile.ProfileRequest;
import com.dev.backendapi.io.profile.ProfileResponse;

@Component
public class ProfileMapper {

    public ProfileResponse toProfileResponse(UserEntity userEntity) {
        return ProfileResponse.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .isAccountVerified(userEntity.getIsAccountVerified())
                .build();
    }

    public UserEntity toUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(request.getPassword())
                .build();
    }
}
