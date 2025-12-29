package com.dev.backendapi.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.dev.backendapi.TestDocumentationListener;
import com.dev.backendapi.io.ProfileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
    "spring.profiles.active=develop",
    "spring.datasource.url=jdbc:mysql://localhost:3307/develop_db?useSSL=false&serverTimezone=UTC"
})
@AutoConfigureWebMvc
class ProfileControllerIntegrationTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully with real database
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        assertNotNull(mockMvc);
    }

    @Test
    void register_WithRealDatabase_ShouldWork() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        ProfileRequest request = new ProfileRequest(
            "Real Data User",
            "realdata@example.com",
            "password123"
        );

        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Real Data User"))
                .andExpect(jsonPath("$.email").value("realdata@example.com"))
                .andExpect(jsonPath("$.isAccountVerified").value(false));
    }

    @Test
    void register_DuplicateEmail_WithRealDatabase_ShouldFail() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // First registration
        ProfileRequest request = new ProfileRequest(
            "First User",
            "duplicate-real@example.com",
            "password123"
        );

        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same email should fail
        ProfileRequest duplicateRequest = new ProfileRequest(
            "Second User",
            "duplicate-real@example.com",
            "password456"
        );

        mockMvc.perform(post("/api/v1.0/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }
}
