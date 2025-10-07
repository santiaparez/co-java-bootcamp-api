package com.example.bootcamp.domain.model;

import java.util.List;

public record PaginatedBootcamp(
        List<CapabilitySummary> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public PaginatedBootcamp {
        content = List.copyOf(content);
    }
}
