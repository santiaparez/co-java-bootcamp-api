package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.model.BootcampPageRequest;
import com.example.bootcamp.domain.model.PaginatedBootcamp;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import reactor.core.publisher.Mono;

public class ListBootcampUseCase {

    private final SpringDataBootcampRepository repository;

    public ListBootcampUseCase(SpringDataBootcampRepository repository) {
        this.repository = repository;
    }

    public Mono<PaginatedBootcamp> execute(BootcampPageRequest request) {
        return repository.findAll(request);
    }
}
