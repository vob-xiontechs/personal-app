package com.dev.backendapi.controller.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination request parameters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    private int page = 0;

    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    private int size = 5;

    private String sortBy = "createdAt";

    private String sortDirection = "desc"; // "asc" or "desc"
}
