package com.dev.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration with auditing support
 *
 * Enables JPA auditing features for automatic population of
 * audit fields like createdBy, updatedBy, createdAt, updatedAt
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
