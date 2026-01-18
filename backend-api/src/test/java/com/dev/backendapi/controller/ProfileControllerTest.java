package com.dev.backendapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dev.backendapi.controller.profile.ProfileController;
import com.dev.backendapi.io.profile.ProfileRequest;
import com.dev.backendapi.io.profile.ProfileResponse;
import com.dev.backendapi.service.profile.ProfileService;
import com.dev.backendapi.TestDocumentationListener;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfileRequest validRequest;
    private ProfileResponse profileResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ProfileRequest("John Doe", "john@example.com", "password123");

        profileResponse = new ProfileResponse(
                UUID.randomUUID().toString(),
                "John Doe",
                "john@example.com",
                false
        );
    }

    @Test
    void register_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        when(profileService.createProfile(any(ProfileRequest.class))).thenReturn(profileResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(profileResponse.getUserId()))
                .andExpect(jsonPath("$.name").value(profileResponse.getName()))
                .andExpect(jsonPath("$.email").value(profileResponse.getEmail()))
                .andExpect(jsonPath("$.isAccountVerified").value(profileResponse.getIsAccountVerified()));
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        ProfileRequest invalidRequest = new ProfileRequest("John Doe", "invalid-email", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        ProfileRequest invalidRequest = new ProfileRequest("", "john@example.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_PasswordTooShort_ReturnsBadRequest() throws Exception {
        // Arrange
        ProfileRequest invalidRequest = new ProfileRequest("John Doe", "john@example.com", "12345");

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_EmailAlreadyExists_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(profileService.createProfile(any(ProfileRequest.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Email already exists"))
                .andExpect(jsonPath("$.type").value("RUNTIME_ERROR"));
    }

    @Test
    void register_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidJson = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WrongContentType_ReturnsUnsupportedMediaType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("UNSUPPORTED_MEDIA_TYPE"));
    }
}
