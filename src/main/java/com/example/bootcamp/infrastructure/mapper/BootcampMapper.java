package com.example.bootcamp.infrastructure.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.repository.documents.BootcampEntity;

import java.time.LocalDate;
import java.util.List;

public final class BootcampMapper {
  private BootcampMapper() {}

  public static Bootcamp toDomain(String id, String name, String description, LocalDate launchDate, Integer durationWeeks, List<String> capabilities) {
    if (durationWeeks == null) {
      throw new IllegalStateException("bootcamp.duration.null");
    }
    return new Bootcamp(
        id,
        name,
        description,
        launchDate,
        durationWeeks,
        capabilities
    );
  }

  public static BootcampEntity toEntity(Bootcamp domain) {
    var entity = new BootcampEntity();
    entity.setId(domain.id());
    entity.setName(domain.name());
    entity.setDescription(domain.description());
    entity.setLaunchDate(domain.launchDate());
    entity.setDurationWeeks(domain.durationWeeks());
    return entity;
  }
}
