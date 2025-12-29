package com.dev.backendapi.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.repository.UserRepository;
import com.dev.backendapi.TestDocumentationListener;

@SpringBootTest(properties = {
    "spring.profiles.active=develop",
    "spring.datasource.url=jdbc:mysql://localhost:3307/develop_db?useSSL=false&serverTimezone=UTC"
})
class ProfileServiceIntegrationTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createProfile_ShouldSaveToDatabase() {
        try {
            // Clean up ALL existing data and reset auto-increment for clean demo
            userRepository.deleteAll();
            jdbcTemplate.execute("ALTER TABLE tbl_users AUTO_INCREMENT = 1");

            // Given
            ProfileRequest request = new ProfileRequest(
                "Integration Test User",
                "integration.test@example.com",
                "password123"
            );

            // Verify no user exists before
            var existingUsers = userRepository.findAll();
            long initialCount = existingUsers.size();

            // When
            ProfileResponse response = profileService.createProfile(request);

            // Then
            assertNotNull(response);
            assertNotNull(response.getUserId());
            assertEquals("Integration Test User", response.getName());
            assertEquals("integration.test@example.com", response.getEmail());
            assertFalse(response.getIsAccountVerified());

            // Verify data was actually saved to database
            var allUsers = userRepository.findAll();
            assertEquals(initialCount + 1, allUsers.size(), "User should be saved to database");

            var savedUser = allUsers.stream()
                .filter(user -> "integration.test@example.com".equals(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("User not found in database"));

            assertEquals("Integration Test User", savedUser.getName());
            assertEquals("integration.test@example.com", savedUser.getEmail());
            assertEquals("password123", savedUser.getPassword());
            assertFalse(savedUser.getIsAccountVerified());
            assertNotNull(savedUser.getUserId());

            // SUCCESS: Don't delete - keep data for demonstration

        } catch (Exception e) {
            // FAILURE: Clean up only if test failed
            try {
                userRepository.findByEmail("integration.test@example.com").ifPresent(user -> {
                    userRepository.delete(user);
                });
            } catch (Exception cleanupException) {
                // Ignore cleanup errors
            }
            throw e; // Re-throw original exception
        }
    }

    @Test
    void createProfile_DuplicateEmail_ShouldThrowException() {
        // Clean up any existing test data first
        userRepository.findByEmail("duplicate@example.com").ifPresent(user -> {
            userRepository.delete(user);
        });

        // Given - First create a user
        ProfileRequest firstRequest = new ProfileRequest(
            "First User",
            "duplicate@example.com",
            "password123"
        );
        profileService.createProfile(firstRequest);

        // When - Try to create with same email
        ProfileRequest duplicateRequest = new ProfileRequest(
            "Second User",
            "duplicate@example.com", // Same email
            "password456"
        );

        // Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> profileService.createProfile(duplicateRequest));
        assertEquals("Email already exists", exception.getMessage());

        // Clean up after test
        userRepository.findByEmail("duplicate@example.com").ifPresent(user -> {
            userRepository.delete(user);
        });
    }
}
