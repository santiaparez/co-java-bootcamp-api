package com.example.bootcamp.domain.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public record Bootcamp(
    String id,
    String name,
    String description,
    LocalDate launchDate,
    int durationWeeks,
    List<String> capabilities
) {
  public Bootcamp {
    if (name == null || name.isBlank() || name.length() > 100) {
      throw new IllegalArgumentException("invalid.bootcamp.name");
    }
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("invalid.bootcamp.description");
    }
    if (launchDate == null) {
      throw new IllegalArgumentException("invalid.bootcamp.launch.date");
    }
    if (durationWeeks <= 0) {
      throw new IllegalArgumentException("invalid.bootcamp.duration");
    }
    if (capabilities == null) {
      throw new IllegalArgumentException("invalid.bootcamp.capabilities.required");
    }

    capabilities = List.copyOf(capabilities);

    if (capabilities.size() < 1) {
      throw new IllegalArgumentException("invalid.bootcamp.capabilities.min");
    }
    if (capabilities.size() > 4) {
      throw new IllegalArgumentException("invalid.bootcamp.capabilities.max");
    }
    if (capabilities.stream().anyMatch(capability -> capability == null || capability.isBlank())) {
      throw new IllegalArgumentException("invalid.bootcamp.capabilities.blank");
    }
    if (new HashSet<>(capabilities).size() != capabilities.size()) {
      throw new IllegalArgumentException("invalid.bootcamp.capabilities.duplicated");
    }
  }
}
