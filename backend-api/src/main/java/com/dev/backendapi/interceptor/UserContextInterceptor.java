package com.dev.backendapi.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor to set user context from header for development/testing purposes.
 * Can be replaced with real authentication middleware when implementing security.
 * Interceptor để set user context từ header cho development/testing.
 * Có thể thay thế bằng authentication middleware thực khi implement security.
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String CURRENT_USER_ATTRIBUTE = "currentUserId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Lấy user ID từ header (cho development/testing)
        String userId = request.getHeader(USER_ID_HEADER);

        if (userId != null && !userId.trim().isEmpty()) {
            // Set vào request attribute để AuditorAwareImpl có thể sử dụng
            request.setAttribute(CURRENT_USER_ATTRIBUTE, userId.trim());
        }

        return true;
    }
}
