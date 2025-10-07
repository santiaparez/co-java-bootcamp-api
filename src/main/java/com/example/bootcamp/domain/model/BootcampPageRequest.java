package com.example.bootcamp.domain.model;

import java.util.Objects;

public record BootcampPageRequest(
        int page,
        int size,
        BootcampSortField sortBy,
        SortDirection direction
) {
    private static final String INVALID_PAGE = "invalid.pagination.page";
    private static final String INVALID_SIZE = "invalid.pagination.size";

    public BootcampPageRequest {
        if (page < 0) {
            throw new IllegalArgumentException(INVALID_PAGE);
        }
        if (size <= 0) {
            throw new IllegalArgumentException(INVALID_SIZE);
        }
        sortBy = Objects.requireNonNull(sortBy, "invalid.pagination.sort.by");
        direction = Objects.requireNonNull(direction, "invalid.pagination.direction");
    }
}
