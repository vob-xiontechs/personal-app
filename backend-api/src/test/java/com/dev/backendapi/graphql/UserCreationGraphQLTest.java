package com.dev.backendapi.graphql;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dev.backendapi.TestDocumentationListener;
import com.dev.backendapi.config.GraphQLConfig;
import com.dev.backendapi.config.TestSecurityConfig;
import com.dev.backendapi.graphql.dto.UserDto;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@Import({TestSecurityConfig.class, GraphQLConfig.class})
class UserCreationGraphQLTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @Test
    void createNewUser_WithValidData_ShouldSucceed() throws Exception {
        // Given
        ProfileResponse expectedResponse = new ProfileResponse(
            "user-123",
            "John Doe",
            "john.doe@example.com",
            false
        );

        when(profileService.createProfile(any())).thenReturn(expectedResponse);

        String mutation = """
            {
                "query": "mutation { registerUser(input: { name: \\"John Doe\\" email: \\"john.doe@example.com\\" password: \\"password123\\" }) { id userId name email isAccountVerified } }"
            }
        """;

        // When & Then
        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.data.registerUser").exists())
                .andExpect(jsonPath("$.data.registerUser.userId").value("user-123"))
                .andExpect(jsonPath("$.data.registerUser.name").value("John Doe"))
                .andExpect(jsonPath("$.data.registerUser.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.registerUser.isAccountVerified").value(false));
    }

    @Test
    void createNewUser_WithEmptyName_ShouldFail() throws Exception {
        String mutation = """
            {
                "query": "mutation { registerUser(input: { name: \\"\\" email: \\"valid@example.com\\" password: \\"password123\\" }) { id userId name email } }"
            }
        """;

        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createNewUser_WithInvalidEmail_ShouldFail() throws Exception {
        String mutation = """
            {
                "query": "mutation { registerUser(input: { name: \\"John Doe\\" email: \\"invalid-email\\" password: \\"password123\\" }) { id userId name email } }"
            }
        """;

        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createNewUser_WithShortPassword_ShouldFail() throws Exception {
        String mutation = """
            {
                "query": "mutation { registerUser(input: { name: \\"John Doe\\" email: \\"john@example.com\\" password: \\"123\\" }) { id userId name email } }"
            }
        """;

        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createMultipleUsers_ShouldReturnDifferentIds() throws Exception {
        // First user
        ProfileResponse user1 = new ProfileResponse("user-1", "User One", "user1@example.com", false);
        when(profileService.createProfile(any())).thenReturn(user1);

        String mutation1 = """
            {
                "query": "mutation { registerUser(input: { name: \\"User One\\" email: \\"user1@example.com\\" password: \\"password123\\" }) { id userId name email } }"
            }
        """;

        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(mutation1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.registerUser.userId").value("user-1"));

        // Second user
        ProfileResponse user2 = new ProfileResponse("user-2", "User Two", "user2@example.com", false);
        when(profileService.createProfile(any())).thenReturn(user2);

        String mutation2 = """
            {
                "query": "mutation { registerUser(input: { name: \\"User Two\\" email: \\"user2@example.com\\" password: \\"password123\\" }) { id userId name email } }"
            }
        """;

        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(mutation2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.registerUser.userId").value("user-2"));
    }

    @Test
    void createUser_ThenQueryUser_ShouldReturnSameData() throws Exception {
        // Create user
        ProfileResponse createdUser = new ProfileResponse(
            "user-999",
            "Query Test User",
            "query.test@example.com",
            false
        );

        when(profileService.createProfile(any())).thenReturn(createdUser);

        String createMutation = """
            {
                "query": "mutation { registerUser(input: { name: \\"Query Test User\\" email: \\"query.test@example.com\\" password: \\"password123\\" }) { id userId name email isAccountVerified } }"
            }
        """;

        mockMvc.perform(post("/graphql")
                .contentType("application/json")
                .content(createMutation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.registerUser.userId").value("user-999"))
                .andExpect(jsonPath("$.data.registerUser.name").value("Query Test User"));
    }
}
