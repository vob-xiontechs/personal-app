package com.dev.backendapi.service.profile;

import java.util.List;

import com.dev.backendapi.io.profile.ProfileRequest;
import com.dev.backendapi.io.profile.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);

    List<ProfileResponse> getProfileList();

    ProfileResponse getProfileDetails(String userId);

    ProfileResponse updateProfile(String userId, ProfileRequest request);

    void deleteProfile(String userId);

    com.dev.backendapi.controller.dto.PaginatedResponse<ProfileResponse> getProfileList(int page, int size, String sortBy, String sortDirection);

}
