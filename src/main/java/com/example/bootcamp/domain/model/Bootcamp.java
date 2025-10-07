package com.example.bootcamp.domain.model;

import java.util.HashSet;
import java.util.List;

public record Bootcamp(String id, String name, String description, List<String> technologies) {
  public Bootcamp {
    if (name == null || name.isBlank() || name.length() > 50) {
      throw new IllegalArgumentException("invalid.tech.name");
    }
    if (description == null || description.isBlank() || description.length() > 90) {
      throw new IllegalArgumentException("invalid.tech.description");
    }
    if (technologies == null) {
      throw new IllegalArgumentException("invalid.bootcamp.technologies.required");
    }

    technologies = List.copyOf(technologies);

    if (technologies.size() < 3) {
      throw new IllegalArgumentException("invalid.bootcamp.technologies.min");
    }
    if (technologies.size() > 20) {
      throw new IllegalArgumentException("invalid.bootcamp.technologies.max");
    }
    if (technologies.stream().anyMatch(t -> t == null || t.isBlank())) {
      throw new IllegalArgumentException("invalid.bootcamp.technologies.blank");
    }
    if (new HashSet<>(technologies).size() != technologies.size()) {
      throw new IllegalArgumentException("invalid.bootcamp.technologies.duplicated");
    }
  }
}
