package com.example.bootcamp.domain.model;

import java.time.LocalDate;
import java.util.List;

public record BootcampSummary(
        String id,
        String name,
        String description,
        LocalDate launchDate,
        int durationWeeks,
        List<CapabilitySummary> capabilities,
        int capabilityCount
) {
    public BootcampSummary {
        capabilities = List.copyOf(capabilities);
    }
}
