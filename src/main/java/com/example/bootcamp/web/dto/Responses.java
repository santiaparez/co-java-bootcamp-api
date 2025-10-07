package com.example.bootcamp.web.dto;

public class Responses {
  public record IdResponse(String id) {}
  public record TechnologyResponse(String id, String name) {}
  public record BootcampResponse(String id, String name, String description, java.util.List<TechnologyResponse> technologies) {}
  public record BootcampPageResponse(java.util.List<BootcampResponse> content, int page, int size, long totalElements, int totalPages) {}
}
