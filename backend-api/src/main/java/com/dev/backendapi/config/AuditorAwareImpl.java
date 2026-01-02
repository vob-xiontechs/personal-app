package com.dev.backendapi.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Implementation for providing current user information for JPA auditing
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String DEFAULT_SYSTEM_AUDITOR = "SYSTEM";
    private static final String CURRENT_USER_ATTRIBUTE = "currentUserId";

    @Override
    public Optional<String> getCurrentAuditor() {
        String currentUser = getCurrentUserFromRequest();
        return Optional.of(currentUser != null ? currentUser : DEFAULT_SYSTEM_AUDITOR);
    }

    private String getCurrentUserFromRequest() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                if (request != null) {
                    Object userIdAttribute = request.getAttribute(CURRENT_USER_ATTRIBUTE);
                    if (userIdAttribute instanceof String) {
                        return (String) userIdAttribute;
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle any request processing errors
        }
        return null;
    }
}
