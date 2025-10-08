package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreateBootcampUseCaseTest {
  @Test
  void create_ok(){
    SpringDataBootcampRepository repo = Mockito.mock(SpringDataBootcampRepository.class);
    Mockito.when(repo.findByName("My Bootcamp")).thenReturn(Mono.empty());
    Mockito.when(repo.save(Mockito.any(Bootcamp.class))).thenAnswer(i -> Mono.just((Bootcamp) i.getArguments()[0]));
    var uc = new CreateBootcampUseCase(repo);
    var capabilities = java.util.List.of("c1", "c2");
    var launchDate = LocalDate.of(2024, 2, 1);
    StepVerifier.create(uc.execute("My Bootcamp", "des", launchDate, 8, capabilities))
        .assertNext(bootcamp -> {
          assertNotNull(bootcamp.id());
          assertEquals("My Bootcamp", bootcamp.name());
          assertEquals(launchDate, bootcamp.launchDate());
          assertEquals(8, bootcamp.durationWeeks());
          assertEquals(capabilities, bootcamp.capabilities());
        })
        .verifyComplete();
  }

  @Test
  void create_conflict_when_name_exists(){
    SpringDataBootcampRepository repo = Mockito.mock(SpringDataBootcampRepository.class);
    Mockito.when(repo.findByName("My Bootcamp")).thenReturn(Mono.just(new Bootcamp("id", "My Bootcamp", "des", LocalDate.of(2024,1,1), 10, java.util.List.of("c1","c2"))));
    var uc = new CreateBootcampUseCase(repo);

    StepVerifier.create(uc.execute("My Bootcamp", "des", LocalDate.of(2024, 1, 1), 10, java.util.List.of("c1", "c2")))
        .expectErrorSatisfies(error -> {
          assertInstanceOf(DomainException.class, error);
          assertEquals("bootcamp.name.already.exists", error.getMessage());
        })
        .verify();
  }

  @Test
  void create_validation_error_when_capabilities_invalid(){
    SpringDataBootcampRepository repo = Mockito.mock(SpringDataBootcampRepository.class);
    Mockito.when(repo.findByName("My Bootcamp")).thenReturn(Mono.empty());
    var uc = new CreateBootcampUseCase(repo);

    StepVerifier.create(uc.execute("My Bootcamp", "des", LocalDate.of(2024, 1, 1), 5, java.util.List.of()))
        .expectErrorSatisfies(error -> {
          assertInstanceOf(DomainException.class, error);
          assertEquals("invalid.bootcamp.capabilities.min", error.getMessage());
        })
        .verify();
  }
}
