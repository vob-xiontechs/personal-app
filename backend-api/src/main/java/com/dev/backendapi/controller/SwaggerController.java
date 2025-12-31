package com.dev.backendapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/swagger")
    public String swaggerRedirect() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/api-docs")
    public String apiDocsRedirect() {
        return "redirect:/v3/api-docs";
    }
}
