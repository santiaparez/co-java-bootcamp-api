package com.example.bootcamp.domain.model;

public record BootcampPageRequest(
        int page,
        int size,
        BootcampSortField sortBy,
        SortDirection direction
) {}
