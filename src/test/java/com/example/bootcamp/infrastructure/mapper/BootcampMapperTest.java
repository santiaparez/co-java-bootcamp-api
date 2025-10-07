package com.example.bootcamp.infrastructure.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.repository.documents.BootcampEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BootcampMapperTest {

    @Test
    void toDomain_mapsAllFields() {
        LocalDate launchDate = LocalDate.of(2024, 5, 10);
        Bootcamp domain = BootcampMapper.toDomain(
                "bootcamp-123",
                "Java Fundamentals",
                "description",
                launchDate,
                8,
                List.of("c1", "c2")
        );

        assertEquals("bootcamp-123", domain.id());
        assertEquals("Java Fundamentals", domain.name());
        assertEquals("description", domain.description());
        assertEquals(launchDate, domain.launchDate());
        assertEquals(8, domain.durationWeeks());
        assertEquals(List.of("c1", "c2"), domain.capabilities());
    }

    @Test
    void toDomain_throwsWhenDurationIsNull() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> BootcampMapper.toDomain(
                        "id",
                        "name",
                        "description",
                        LocalDate.now(),
                        null,
                        List.of("capability")
                )
        );

        assertEquals("bootcamp.duration.null", exception.getMessage());
    }

    @Test
    void toEntity_copiesPrimitiveFields() {
        Bootcamp bootcamp = new Bootcamp(
                "bootcamp-456",
                "Java Advanced",
                "advanced description",
                LocalDate.of(2025, 1, 1),
                12,
                List.of("c1")
        );

        BootcampEntity entity = BootcampMapper.toEntity(bootcamp);

        assertEquals("bootcamp-456", entity.getId());
        assertEquals("Java Advanced", entity.getName());
        assertEquals("advanced description", entity.getDescription());
        assertEquals(LocalDate.of(2025, 1, 1), entity.getLaunchDate());
        assertEquals(12, entity.getDurationWeeks());
    }
}
