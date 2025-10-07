package com.example.bootcamp.infrastructure.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.repository.documents.BootcampEntity;

import java.util.List;

public class TechnologiesMapper {
  private TechnologiesMapper() {}

  public static Bootcamp toDomain(String id, String name, String description, List<String> technologies){
    return new Bootcamp(
            id,
            name,
            description,
            technologies
    );
  }

  public static BootcampEntity toEntity(Bootcamp domain){
    var entity = new BootcampEntity();
    entity.setId(domain.id());
    entity.setName(domain.name());
    entity.setDescription(domain.description());
    return entity;
  }
}

