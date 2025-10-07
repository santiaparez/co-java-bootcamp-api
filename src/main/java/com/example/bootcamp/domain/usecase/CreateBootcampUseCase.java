package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.error.ErrorCodes;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public class CreateBootcampUseCase {
  private final SpringDataBootcampRepository repo;
  public CreateBootcampUseCase(SpringDataBootcampRepository repo) { this.repo = repo; }
  public Mono<Bootcamp> execute(String name, String description, List<String> technologies) {
    return repo.findByName(name)
        .flatMap(existing -> Mono.<Bootcamp>error(
            new DomainException(ErrorCodes.CONFLICT, "bootcamp.name.already.exists")
        ))
        .switchIfEmpty(Mono.defer(() -> {
          try {
            Bootcamp bootcamp = new Bootcamp(UUID.randomUUID().toString(), name, description, technologies);
            return repo.save(bootcamp);
          } catch (IllegalArgumentException ex) {
            return Mono.error(new DomainException(ErrorCodes.VALIDATION_ERROR, ex.getMessage()));
          }
        }));
  }
}
