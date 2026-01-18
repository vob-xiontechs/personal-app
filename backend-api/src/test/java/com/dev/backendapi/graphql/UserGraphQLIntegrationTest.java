package com.dev.backendapi.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.dev.backendapi.TestDocumentationListener;
import com.dev.backendapi.service.profile.ProfileService;

@GraphQlTest(UserController.class)
class UserGraphQLIntegrationTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private ProfileService profileService;

    @Test
    void userQuery_ShouldReturnUser() {
        String query = """
            query {
                user(id: 1) {
                    id
                    userId
                    name
                    email
                    isAccountVerified
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("user")
                .entity(UserResponse.class)
                .satisfies(user -> {
                    assertNotNull(user.id);
                    assertEquals("user-1", user.userId);
                    assertEquals("User 1", user.name);
                    assertEquals("user1@example.com", user.email);
                    assertTrue(user.isAccountVerified);
                });
    }

    @Test
    void usersQuery_ShouldReturnPaginatedUsers() {
        String query = """
            query {
                users(page: 0, size: 10) {
                    content {
                        id
                        userId
                        name
                        email
                        isAccountVerified
                    }
                    totalElements
                    totalPages
                    size
                    number
                    first
                    last
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("users")
                .entity(UserConnection.class)
                .satisfies(connection -> {
                    assertNotNull(connection.content);
                    assertEquals(3, connection.content.size());
                    assertEquals(3L, connection.totalElements);
                    assertEquals(1, connection.totalPages);
                    assertEquals(10, connection.size);
                    assertEquals(0, connection.number);
                    assertTrue(connection.first);
                    assertTrue(connection.last);

                    // Check first user
                    UserResponse firstUser = connection.content.get(0);
                    assertEquals("user-1", firstUser.userId);
                    assertEquals("John Doe", firstUser.name);
                    assertEquals("john@example.com", firstUser.email);
                    assertTrue(firstUser.isAccountVerified);
                });
    }

    @Test
    void meQuery_ShouldReturnCurrentUser() {
        String query = """
            query {
                me {
                    id
                    userId
                    name
                    email
                    isAccountVerified
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("me")
                .entity(UserResponse.class)
                .satisfies(user -> {
                    assertNotNull(user.id);
                    assertEquals("current-user", user.userId);
                    assertEquals("Current User", user.name);
                    assertEquals("current@example.com", user.email);
                    assertTrue(user.isAccountVerified);
                });
    }

    @Test
    void registerUserMutation_ShouldCreateUser() {
        String mutation = """
            mutation {
                registerUser(input: {
                    name: "GraphQL Test User"
                    email: "graphql.test@example.com"
                    password: "password123"
                }) {
                    id
                    userId
                    name
                    email
                    isAccountVerified
                }
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("registerUser")
                .entity(UserResponse.class)
                .satisfies(user -> {
                    assertNotNull(user.id);
                    assertNotNull(user.userId);
                    assertEquals("GraphQL Test User", user.name);
                    assertEquals("graphql.test@example.com", user.email);
                    assertFalse(user.isAccountVerified); // New users are not verified by default
                });
    }

    @Test
    void updateUserMutation_ShouldUpdateUser() {
        String mutation = """
            mutation {
                updateUser(id: 1, input: {
                    name: "Updated Name"
                    email: "updated@example.com"
                    isAccountVerified: true
                }) {
                    id
                    userId
                    name
                    email
                    isAccountVerified
                }
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("updateUser")
                .entity(UserResponse.class)
                .satisfies(user -> {
                    assertNotNull(user.id);
                    assertEquals("user-1", user.userId);
                    assertEquals("Updated Name", user.name);
                    assertEquals("updated@example.com", user.email);
                    assertTrue(user.isAccountVerified);
                });
    }

    @Test
    void deleteUserMutation_ShouldReturnTrue() {
        String mutation = """
            mutation {
                deleteUser(id: 1)
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    // Response DTOs
    public static class UserResponse {
        public String id;
        public String userId;
        public String name;
        public String email;
        public Boolean isAccountVerified;
    }

    public static class UserConnection {
        public java.util.List<UserResponse> content;
        public Long totalElements;
        public Integer totalPages;
        public Integer size;
        public Integer number;
        public Boolean first;
        public Boolean last;
    }
}
