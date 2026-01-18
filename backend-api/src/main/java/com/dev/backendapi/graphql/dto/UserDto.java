package com.dev.backendapi.graphql.dto;

import java.time.LocalDateTime;

import com.dev.backendapi.io.profile.ProfileResponse;

public record UserDto(
    String id,
    String userId,
    String name,
    String email,
    Boolean isAccountVerified,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserDto fromProfileResponse(ProfileResponse profile) {
        return new UserDto(
            profile.getUserId(), // Use userId as id for GraphQL
            profile.getUserId(),
            profile.getName(),
            profile.getEmail(),
            profile.getIsAccountVerified(),
            LocalDateTime.now(), // Mock createdAt
            LocalDateTime.now()  // Mock updatedAt
        );
    }
}
