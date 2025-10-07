package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.error.ErrorCodes;
import com.example.bootcamp.domain.model.BootcampPageRequest;
import com.example.bootcamp.domain.model.BootcampSortField;
import com.example.bootcamp.domain.model.PaginatedBootcamp;
import com.example.bootcamp.domain.model.SortDirection;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

class ListBootcampUseCaseTest {

    private final SpringDataBootcampRepository repository = Mockito.mock(SpringDataBootcampRepository.class);
    private final ListBootcampUseCase useCase = new ListBootcampUseCase(repository);

    @Test
    void delegatesToRepository() {
        BootcampPageRequest request = new BootcampPageRequest(0, 5, BootcampSortField.NAME, SortDirection.ASC);
        PaginatedBootcamp page = new PaginatedBootcamp(Collections.emptyList(), 0, 5, 0, 0);
        Mockito.when(repository.findAll(request)).thenReturn(Mono.just(page));

        StepVerifier.create(useCase.execute(request))
                .expectNext(page)
                .verifyComplete();

        Mockito.verify(repository).findAll(request);
    }

    @Test
    void failsForInvalidSize() {
        BootcampPageRequest request = new BootcampPageRequest(0, 0, BootcampSortField.NAME, SortDirection.ASC);

        StepVerifier.create(useCase.execute(request))
                .expectErrorSatisfies(error -> {
                    org.junit.jupiter.api.Assertions.assertInstanceOf(DomainException.class, error);
                    DomainException ex = (DomainException) error;
                    org.junit.jupiter.api.Assertions.assertEquals(ErrorCodes.VALIDATION_ERROR, ex.getCode());
                })
                .verify();

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void failsForNegativePage() {
        BootcampPageRequest request = new BootcampPageRequest(-1, 5, BootcampSortField.NAME, SortDirection.ASC);

        StepVerifier.create(useCase.execute(request))
                .expectError(DomainException.class)
                .verify();

        Mockito.verifyNoInteractions(repository);
    }
}
