package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.error.ErrorCodes;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import reactor.core.publisher.Mono;

public class DeleteBootcampUseCase {
  private final SpringDataBootcampRepository repository;

  public DeleteBootcampUseCase(SpringDataBootcampRepository repository) {
    this.repository = repository;
  }

  public Mono<Void> execute(String bootcampId) {
    return repository.findById(bootcampId)
        .switchIfEmpty(Mono.error(new DomainException(ErrorCodes.BOOTCAMP_NOT_FOUND, "bootcamp.not.found")))
        .flatMap(bootcamp -> repository.deleteById(bootcamp.id()));
  }
}
