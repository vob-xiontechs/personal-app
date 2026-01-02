package com.dev.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dev.backendapi.interceptor.UserContextInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * Web configuration to register interceptors.
 * Web configuration để đăng ký interceptors.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký UserContextInterceptor cho tất cả requests
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**") // Áp dụng cho tất cả endpoints
                .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**"); // Loại trừ một số endpoints hệ thống
    }
}
