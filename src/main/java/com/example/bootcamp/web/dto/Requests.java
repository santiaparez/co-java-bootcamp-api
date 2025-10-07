package com.example.bootcamp.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class Requests {
  public record CreateBootcampRequest(
      @NotBlank String name,
      String description,
      @NotNull @Size(min = 3, max = 20) List<@NotBlank String> technologies
  ) {}
}
