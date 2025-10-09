package com.example.bootcamp.infrastructure.client;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.BootcampSummary;
import com.example.bootcamp.domain.model.CapabilitySummary;
import com.example.bootcamp.domain.model.TechnologySummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BootcampReportClient {

  private final WebClient webClient;
  private final String reportPath;

  public BootcampReportClient(
      WebClient.Builder webClientBuilder,
      @Value("${app.bootcamp-report.base-url:http://localhost:8085}") String baseUrl,
      @Value("${app.bootcamp-report.path:/bootcamp-reports}") String reportPath
  ) {
    this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    this.reportPath = reportPath;
  }

  public Mono<Void> sendBootcampReport(Bootcamp bootcamp, BootcampSummary summary) {
    BootcampReportRequest request = BootcampReportRequest.from(bootcamp, summary);
    return webClient.post()
        .uri(reportPath)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(Void.class);
  }

  private record BootcampReportRequest(
      String bootcampId,
      String name,
      String description,
      List<CapabilityPayload> capacities,
      List<TechnologyPayload> technologies,
      List<ParticipantPayload> participants
  ) {
    private static BootcampReportRequest from(Bootcamp bootcamp, BootcampSummary summary) {
      List<CapabilityPayload> capabilityPayloads = summary.capabilities().stream()
          .map(capability -> new CapabilityPayload(
              capability.id(),
              capability.name(),
              capability.description()
          ))
          .toList();

      Map<String, TechnologySummary> technologies = new LinkedHashMap<>();
      for (CapabilitySummary capability : summary.capabilities()) {
        for (TechnologySummary technology : capability.technologies()) {
          technologies.putIfAbsent(technology.id(), technology);
        }
      }

      List<TechnologyPayload> technologyPayloads = technologies.values().stream()
          .map(technology -> new TechnologyPayload(technology.id(), technology.name()))
          .toList();

      return new BootcampReportRequest(
          bootcamp.id(),
          bootcamp.name(),
          bootcamp.description(),
          capabilityPayloads,
          technologyPayloads,
          List.of()
      );
    }
  }

  private record CapabilityPayload(String id, String name, String description) {
  }

  private record TechnologyPayload(String id, String name) {
  }

  private record ParticipantPayload(String id, String fullName, String email) {
  }
}
