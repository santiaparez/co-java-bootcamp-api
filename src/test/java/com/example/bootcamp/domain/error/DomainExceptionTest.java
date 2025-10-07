package com.example.bootcamp.domain.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

  @Test
  void exposesCodeAndMessage() {
    DomainException exception = new DomainException(ErrorCodes.BRANCH_NOT_FOUND, "branch.not.found");

    assertEquals(ErrorCodes.BRANCH_NOT_FOUND, exception.getCode());
    assertEquals("branch.not.found", exception.getMessage());
  }
}

