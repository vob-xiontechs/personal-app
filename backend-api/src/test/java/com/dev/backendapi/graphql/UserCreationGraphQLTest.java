package com.dev.backendapi.graphql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import com.dev.backendapi.TestDocumentationListener;
import com.dev.backendapi.config.GraphQLConfig;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.service.ProfileService;

@GraphQlTest(UserController.class)
@Import(GraphQLConfig.class)
class UserCreationGraphQLTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private ProfileService profileService;

    @Test
    void createNewUser_WithValidData_ShouldSucceed() {
        // Given
        ProfileResponse expectedResponse = new ProfileResponse(
            "user-123",
            "John Doe",
            "john.doe@example.com",
            false
        );

        when(profileService.createProfile(any())).thenReturn(expectedResponse);

        // When & Then
        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                    isAccountVerified
                }
            }
        """)
        .variable("input", new RegisterUserInput("John Doe", "john.doe@example.com", "password123"))
        .execute()
        .path("registerUser")
        .matchesJson("""
            {
                "id": "user-123",
                "userId": "user-123",
                "name": "John Doe",
                "email": "john.doe@example.com",
                "isAccountVerified": false
            }
        """);
    }

    @Test
    void createNewUser_WithEmptyName_ShouldFail() {
        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                }
            }
        """)
        .variable("input", new RegisterUserInput("", "valid@example.com", "password123"))
        .execute()
        .errors()
        .expect(e -> e.getMessage().contains("name") || e.getMessage().contains("empty"));
    }

    @Test
    void createNewUser_WithInvalidEmail_ShouldFail() {
        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                }
            }
        """)
        .variable("input", new RegisterUserInput("John Doe", "invalid-email", "password123"))
        .execute()
        .errors()
        .expect(e -> e.getMessage().contains("email") || e.getMessage().contains("invalid"));
    }

    @Test
    void createNewUser_WithShortPassword_ShouldFail() {
        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                }
            }
        """)
        .variable("input", new RegisterUserInput("John Doe", "john@example.com", "123"))
        .execute()
        .errors()
        .expect(e -> e.getMessage().contains("password") || e.getMessage().contains("length"));
    }

    @Test
    void createMultipleUsers_ShouldReturnDifferentIds() {
        // First user
        ProfileResponse user1 = new ProfileResponse("user-1", "User One", "user1@example.com", false);
        when(profileService.createProfile(any())).thenReturn(user1);

        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                }
            }
        """)
        .variable("input", new RegisterUserInput("User One", "user1@example.com", "password123"))
        .execute()
        .path("registerUser.userId")
        .entity(String.class)
        .isEqualTo("user-1");

        // Second user
        ProfileResponse user2 = new ProfileResponse("user-2", "User Two", "user2@example.com", false);
        when(profileService.createProfile(any())).thenReturn(user2);

        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                }
            }
        """)
        .variable("input", new RegisterUserInput("User Two", "user2@example.com", "password123"))
        .execute()
        .path("registerUser.userId")
        .entity(String.class)
        .isEqualTo("user-2");
    }

    @Test
    void createUser_ThenQueryUser_ShouldReturnSameData() {
        // Create user
        ProfileResponse createdUser = new ProfileResponse(
            "user-999",
            "Query Test User",
            "query.test@example.com",
            false
        );

        when(profileService.createProfile(any())).thenReturn(createdUser);

        graphQlTester.document("""
            mutation RegisterUser($input: RegisterUserInput!) {
                registerUser(input: $input) {
                    id
                    userId
                    name
                    email
                    isAccountVerified
                }
            }
        """)
        .variable("input", new RegisterUserInput("Query Test User", "query.test@example.com", "password123"))
        .execute()
        .path("registerUser")
        .matchesJson("""
            {
                "id": "user-999",
                "userId": "user-999",
                "name": "Query Test User",
                "email": "query.test@example.com",
                "isAccountVerified": false
            }
        """);
    }

    // Input record class
    public record RegisterUserInput(String name, String email, String password) {}
}
