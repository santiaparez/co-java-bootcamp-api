package com.example.bootcamp.domain.error;

public class DomainException extends RuntimeException {
  private final ErrorCodes code;
  public DomainException(ErrorCodes code, String message) { super(message); this.code = code; }
  public ErrorCodes getCode() { return code; }
}
