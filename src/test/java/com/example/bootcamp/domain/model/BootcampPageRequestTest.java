package com.example.bootcamp.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootcampPageRequestTest {

    @Test
    void createBootcampPageRequest() {
        BootcampPageRequest request = new BootcampPageRequest(1, 20, BootcampSortField.NAME, SortDirection.ASC);

        assertEquals(1, request.page());
        assertEquals(20, request.size());
        assertEquals(BootcampSortField.NAME, request.sortBy());
        assertEquals(SortDirection.ASC, request.direction());
    }

    @Test
    void createBootcampPageRequest_invalidPage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BootcampPageRequest(-1, 10, BootcampSortField.NAME, SortDirection.ASC)
        );

        assertEquals("invalid.pagination.page", exception.getMessage());
    }

    @Test
    void createBootcampPageRequest_invalidSize() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new BootcampPageRequest(0, 0, BootcampSortField.NAME, SortDirection.ASC)
        );

        assertEquals("invalid.pagination.size", exception.getMessage());
    }

    @Test
    void createBootcampPageRequest_invalidSortField() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new BootcampPageRequest(0, 10, null, SortDirection.ASC)
        );

        assertEquals("invalid.pagination.sort.by", exception.getMessage());
    }

    @Test
    void createBootcampPageRequest_invalidSortDirection() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new BootcampPageRequest(0, 10, BootcampSortField.NAME, null)
        );

        assertEquals("invalid.pagination.direction", exception.getMessage());
    }
}
