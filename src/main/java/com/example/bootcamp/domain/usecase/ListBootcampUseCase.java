package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.error.ErrorCodes;
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
        if (request.size() <= 0) {
            return Mono.error(new DomainException(ErrorCodes.VALIDATION_ERROR, "invalid.pagination.size"));
        }
        if (request.page() < 0) {
            return Mono.error(new DomainException(ErrorCodes.VALIDATION_ERROR, "invalid.pagination.page"));
        }
        return repository.findAll(request);
    }
}
