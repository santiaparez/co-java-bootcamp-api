package com.example.bootcamp.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class Requests {
  public record CreateBootcampRequest(
      @NotBlank String name,
      @NotBlank String description,
      @NotNull LocalDate launchDate,
      @Min(1) int durationWeeks,
      @NotNull @Size(min = 1, max = 4) List<@NotBlank String> capabilities
  ) {}
}
