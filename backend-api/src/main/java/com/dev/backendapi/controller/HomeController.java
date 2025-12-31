package com.dev.backendapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Personal App Backend API! 🚀\n\nAvailable endpoints:\n- POST /api/v1.0/register\n- POST /graphql\n- GET /actuator/health\n\nAPI Documentation:\n- Swagger UI: /swagger-ui.html\n- OpenAPI JSON: /v3/api-docs");
    }
}
