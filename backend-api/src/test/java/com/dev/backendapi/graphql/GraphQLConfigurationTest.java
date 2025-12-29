package com.dev.backendapi.graphql;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.dev.backendapi.TestDocumentationListener;

@SpringBootTest(properties = {
    "spring.profiles.active=develop",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration"
})
class GraphQLConfigurationTest {

    @RegisterExtension
    static TestDocumentationListener testDocumentationListener = new TestDocumentationListener();

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads_WithGraphQLExcluded() {
        assertNotNull(context);
        // Test that context loads without GraphQL auto-configuration
    }

    @Test
    void graphQLBeans_ShouldBeAvailable() {
        // Verify that our GraphQL beans are available
        boolean hasGraphQLController = context.containsBean("userController");
        assertTrue(hasGraphQLController, "UserController should be available");
    }
}
