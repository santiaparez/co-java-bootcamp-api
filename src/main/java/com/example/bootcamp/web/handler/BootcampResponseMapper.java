package com.example.bootcamp.web.handler;

import com.example.bootcamp.domain.model.BootcampSummary;
import com.example.bootcamp.domain.model.CapabilitySummary;
import com.example.bootcamp.domain.model.PaginatedBootcamp;
import com.example.bootcamp.domain.model.TechnologySummary;
import com.example.bootcamp.web.dto.Responses.BootcampPageResponse;
import com.example.bootcamp.web.dto.Responses.BootcampResponse;
import com.example.bootcamp.web.dto.Responses.CapabilityResponse;
import com.example.bootcamp.web.dto.Responses.TechnologyResponse;

final class BootcampResponseMapper {
  private BootcampResponseMapper() {
  }

  static BootcampPageResponse page(PaginatedBootcamp page) {
    return new BootcampPageResponse(
        page.content().stream().map(BootcampResponseMapper::bootcamp).toList(),
        page.page(),
        page.size(),
        page.totalElements(),
        page.totalPages()
    );
  }

  static BootcampResponse bootcamp(BootcampSummary summary){
    return new BootcampResponse(
        summary.id(),
        summary.name(),
        summary.description(),
        summary.launchDate(),
        summary.durationWeeks(),
        summary.capabilities().stream().map(BootcampResponseMapper::capability).toList(),
        summary.capabilityCount()
    );
  }

  static CapabilityResponse capability(CapabilitySummary capability) {
    return new CapabilityResponse(
        capability.id(),
        capability.name(),
        capability.description(),
        capability.technologies().stream().map(BootcampResponseMapper::technology).toList(),
        capability.technologyCount()
    );
  }

  static TechnologyResponse technology(TechnologySummary technology) {
    return new TechnologyResponse(technology.id(), technology.name());
  }
}
