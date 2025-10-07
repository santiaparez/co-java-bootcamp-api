package com.example.bootcamp.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootcampTest {

  @Test
  void createBootcamp_ok() {
    var launchDate = java.time.LocalDate.of(2024, 1, 1);
    Bootcamp bootcamp = new Bootcamp("f-1", "Acme", "des", launchDate, 12, java.util.List.of("c1", "c2"));

    assertEquals("f-1", bootcamp.id());
    assertEquals("Acme", bootcamp.name());
    assertEquals("des", bootcamp.description());
    assertEquals(launchDate, bootcamp.launchDate());
    assertEquals(12, bootcamp.durationWeeks());
    assertEquals(java.util.List.of("c1", "c2"), bootcamp.capabilities());
  }


}

