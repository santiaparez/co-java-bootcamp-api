package com.example.bootcamp.domain.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

  @Test
  void exposesCodeAndMessage() {
    DomainException exception = new DomainException(ErrorCodes.TECHNOLOGY_NOT_FOUND, "technology.not.found");

    assertEquals(ErrorCodes.TECHNOLOGY_NOT_FOUND, exception.getCode());
    assertEquals("technology.not.found", exception.getMessage());
  }
}

