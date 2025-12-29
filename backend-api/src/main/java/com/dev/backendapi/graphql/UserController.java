package com.dev.backendapi.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.dev.backendapi.graphql.dto.UserDto;
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
    public UserDto user(@Argument Long id) {
        // For simplicity, returning a mock user - in real implementation,
        // you would fetch from database by ID
        ProfileResponse profile = new ProfileResponse(
            "user-" + id,
            "User " + id,
            "user" + id + "@example.com",
            true
        );
        return UserDto.fromProfileResponse(profile);
    }

    @QueryMapping
    public UserConnectionDto users(@Argument Integer page, @Argument Integer size) {
        // For demonstration, creating mock data
        // In real implementation, this would use proper pagination from repository
        List<UserDto> mockUsers = List.of(
            UserDto.fromProfileResponse(new ProfileResponse("user-1", "John Doe", "john@example.com", true)),
            UserDto.fromProfileResponse(new ProfileResponse("user-2", "Jane Smith", "jane@example.com", false)),
            UserDto.fromProfileResponse(new ProfileResponse("user-3", "Bob Johnson", "bob@example.com", true))
        );

        return new UserConnectionDto(
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
    public UserDto me() {
        // In real implementation, this would return the authenticated user
        ProfileResponse profile = new ProfileResponse(
            "current-user",
            "Current User",
            "current@example.com",
            true
        );
        return UserDto.fromProfileResponse(profile);
    }

    @MutationMapping
    public UserDto registerUser(@Argument RegisterUserInput input) {
        ProfileRequest request = new ProfileRequest(
            input.name(),
            input.email(),
            input.password()
        );

        ProfileResponse profile = profileService.createProfile(request);
        return UserDto.fromProfileResponse(profile);
    }

    @MutationMapping
    public UserDto updateUser(@Argument Long id, @Argument UpdateUserInput input) {
        // For demonstration - in real implementation, this would update the user
        ProfileResponse profile = new ProfileResponse(
            "user-" + id,
            input.name() != null ? input.name() : "Updated User",
            input.email() != null ? input.email() : "updated@example.com",
            input.isAccountVerified() != null ? input.isAccountVerified() : true
        );
        return UserDto.fromProfileResponse(profile);
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
    public record UserConnectionDto(
        List<UserDto> content,
        Long totalElements,
        Integer totalPages,
        Integer size,
        Integer number,
        Boolean first,
        Boolean last
    ) {}
}
