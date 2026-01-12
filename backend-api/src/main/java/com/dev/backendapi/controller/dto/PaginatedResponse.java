package com.dev.backendapi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    // Calculated fields
    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return page == totalPages - 1;
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
}
