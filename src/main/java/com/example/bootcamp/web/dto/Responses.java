package com.example.bootcamp.web.dto;

public class Responses {
  public record IdResponse(String id) {}
  public record TechnologyResponse(String id, String name) {}
  public record CapabilityResponse(String id, String name, String description, java.util.List<TechnologyResponse> technologies, int technologyCount) {}
  public record BootcampResponse(String id, String name, String description, java.time.LocalDate launchDate, int durationWeeks, java.util.List<CapabilityResponse> capabilities, int capabilityCount) {}
  public record BootcampPageResponse(java.util.List<BootcampResponse> content, int page, int size, long totalElements, int totalPages) {}
}
