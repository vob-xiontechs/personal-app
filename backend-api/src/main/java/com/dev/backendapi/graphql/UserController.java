package com.dev.backendapi.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.dev.backendapi.io.ProfileRequest;
import com.dev.backendapi.io.ProfileResponse;
import com.dev.backendapi.service.ProfileService;

@Controller
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @QueryMapping
    public ProfileResponse user(@Argument Long id) {
        // For simplicity, returning a mock user - in real implementation,
        // you would fetch from database by ID
        return new ProfileResponse(
            "user-" + id,
            "User " + id,
            "user" + id + "@example.com",
            true
        );
    }

    @QueryMapping
    public UserConnection users(@Argument Integer page, @Argument Integer size) {
        // For demonstration, creating mock data
        // In real implementation, this would use proper pagination from repository
        List<ProfileResponse> mockUsers = List.of(
            new ProfileResponse("user-1", "John Doe", "john@example.com", true),
            new ProfileResponse("user-2", "Jane Smith", "jane@example.com", false),
            new ProfileResponse("user-3", "Bob Johnson", "bob@example.com", true)
        );

        return new UserConnection(
            mockUsers,
            3L,
            1,
            size != null ? size : 10,
            page != null ? page : 0,
            true,
            true
        );
    }

    @QueryMapping
    public ProfileResponse me() {
        // In real implementation, this would return the authenticated user
        return new ProfileResponse(
            "current-user",
            "Current User",
            "current@example.com",
            true
        );
    }

    @MutationMapping
    public ProfileResponse registerUser(@Argument RegisterUserInput input) {
        ProfileRequest request = new ProfileRequest(
            input.name(),
            input.email(),
            input.password()
        );

        return profileService.createProfile(request);
    }

    @MutationMapping
    public ProfileResponse updateUser(@Argument Long id, @Argument UpdateUserInput input) {
        // For demonstration - in real implementation, this would update the user
        return new ProfileResponse(
            "user-" + id,
            input.name() != null ? input.name() : "Updated User",
            input.email() != null ? input.email() : "updated@example.com",
            input.isAccountVerified() != null ? input.isAccountVerified() : true
        );
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        // For demonstration - in real implementation, this would delete the user
        return true;
    }

    // Input record classes
    public record RegisterUserInput(String name, String email, String password) {}
    public record UpdateUserInput(String name, String email, Boolean isAccountVerified) {}

    // Connection class for pagination
    public record UserConnection(
        List<ProfileResponse> content,
        Long totalElements,
        Integer totalPages,
        Integer size,
        Integer number,
        Boolean first,
        Boolean last
    ) {}
}
