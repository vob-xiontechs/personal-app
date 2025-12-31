package com.dev.backendapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.service.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "APIs for managing user profiles")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    @Operation(summary = "Register new user profile", description = "Creates a new user profile with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Email already exists",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ProfileResponse> register(@Valid @RequestBody ProfileRequest request){
        ProfileResponse response = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
}
