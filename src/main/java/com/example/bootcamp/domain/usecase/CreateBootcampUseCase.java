package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.error.ErrorCodes;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.client.BootcampReportClient;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateBootcampUseCase {
  private final SpringDataBootcampRepository repo;
  private final BootcampReportClient reportClient;

  public CreateBootcampUseCase(SpringDataBootcampRepository repo, BootcampReportClient reportClient) {
    this.repo = repo;
    this.reportClient = reportClient;
  }
  public Mono<Bootcamp> execute(String name, String description, LocalDate launchDate, int durationWeeks, List<String> capabilities) {
    return repo.findByName(name)
        .flatMap(existing -> Mono.<Bootcamp>error(
            new DomainException(ErrorCodes.CONFLICT, "bootcamp.name.already.exists")
        ))
        .switchIfEmpty(Mono.defer(() -> {
          try {
            Bootcamp bootcamp = new Bootcamp(UUID.randomUUID().toString(), name, description, launchDate, durationWeeks, capabilities);
            return repo.save(bootcamp)
                .flatMap(saved -> repo.findSummaryById(saved.id())
                    .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.INTERNAL, "bootcamp.summary.not.found")))
                    .flatMap(summary -> reportClient.sendBootcampReport(saved, summary).thenReturn(saved))
                );
          } catch (IllegalArgumentException ex) {
            return Mono.error(new DomainException(ErrorCodes.VALIDATION_ERROR, ex.getMessage()));
          }
        }));
  }
}
