package com.dev.backendapi.service.profile;

import com.dev.backendapi.io.profile.ProfileResponse;
import com.dev.backendapi.io.profile.ProfileRequest;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);

}
