package com.dev.backendapi.service;

import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;

public interface  ProfileService {

    ProfileResponse createProfile(ProfileRequest request);
    
}
