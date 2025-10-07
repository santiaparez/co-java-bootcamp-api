package com.example.bootcamp.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootcampTest {

  @Test
  void createBootcamp_ok() {
    Bootcamp bootcamp = new Bootcamp("f-1", "Acme", "des", java.util.List.of("t1", "t2", "t3"));

    assertEquals("f-1", bootcamp.id());
    assertEquals("Acme", bootcamp.name());
    assertEquals("des", bootcamp.description());
    assertEquals(java.util.List.of("t1", "t2", "t3"), bootcamp.technologies());
  }


}

