package com.dev.backendapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dev.backendapi.entity.UserEntity;
import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.repository.UserRepository;
import com.dev.backendapi.TestDocumentationListener;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileRequest request;
    private UserEntity userEntity;
    private ProfileResponse response;

    @BeforeEach
    void setUp() {
        request = new ProfileRequest("John Doe", "john@example.com", "password123");

        userEntity = UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();

        response = new ProfileResponse(userEntity.getUserId(), userEntity.getName(),
                userEntity.getEmail(), userEntity.getIsAccountVerified());
    }

    @Test
    void createProfile_Success() {
        // Arrange
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        ProfileResponse result = profileService.createProfile(request);

        // Assert
        assertNotNull(result);
        assertEquals(userEntity.getUserId(), result.getUserId());
        assertEquals(userEntity.getName(), result.getName());
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getIsAccountVerified(), result.getIsAccountVerified());

        verify(userRepository).findByEmail(request.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createProfile_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> profileService.createProfile(request));
        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository).findByEmail(request.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void createProfile_SaveFails_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> profileService.createProfile(request));
        assertEquals("Database error", exception.getMessage());

        verify(userRepository).findByEmail(request.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }
}
