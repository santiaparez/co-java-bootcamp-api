package com.example.bootcamp.domain.model;

import java.util.List;

public record CapabilitySummary(
        String id,
        String name,
        String description,
        List<TechnologySummary> technologies,
        int technologyCount
) {
    public CapabilitySummary {
        technologies = List.copyOf(technologies);
    }
}
