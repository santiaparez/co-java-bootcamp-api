package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.usecase.CreateBootcampUseCase;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class CreateBootcampUseCaseTest {
  @Test
  void create_ok(){
    SpringDataBootcampRepository repo = Mockito.mock(SpringDataBootcampRepository.class);
    Mockito.when(repo.findByName("My Bootcamp")).thenReturn(Mono.empty());
    Mockito.when(repo.save(Mockito.any(Bootcamp.class))).thenAnswer(i -> Mono.just((Bootcamp) i.getArguments()[0]));
    var uc = new CreateBootcampUseCase(repo);
    StepVerifier.create(uc.execute("My Bootcamp", "des", java.util.List.of("t1", "t2", "t3")))
        .assertNext(bootcamp -> {
          assertNotNull(bootcamp.id());
          assertEquals("My Bootcamp", bootcamp.name());
          assertEquals(java.util.List.of("t1", "t2", "t3"), bootcamp.technologies());
        })
        .verifyComplete();
  }

  @Test
  void create_conflict_when_name_exists(){
    SpringDataBootcampRepository repo = Mockito.mock(SpringDataBootcampRepository.class);
    Mockito.when(repo.findByName("My Bootcamp")).thenReturn(Mono.just(new Bootcamp("id", "My Bootcamp", "des", java.util.List.of("t1","t2","t3"))));
    var uc = new CreateBootcampUseCase(repo);

    StepVerifier.create(uc.execute("My Bootcamp", "des", java.util.List.of("t1", "t2", "t3")))
        .expectErrorSatisfies(error -> {
          assertInstanceOf(DomainException.class, error);
          assertEquals("bootcamp.name.already.exists", error.getMessage());
        })
        .verify();
  }

  @Test
  void create_validation_error_when_technologies_invalid(){
    SpringDataBootcampRepository repo = Mockito.mock(SpringDataBootcampRepository.class);
    Mockito.when(repo.findByName("My Bootcamp")).thenReturn(Mono.empty());
    var uc = new CreateBootcampUseCase(repo);

    StepVerifier.create(uc.execute("My Bootcamp", "des", java.util.List.of("t1", "t2")))
        .expectErrorSatisfies(error -> {
          assertInstanceOf(DomainException.class, error);
          assertEquals("invalid.bootcamp.technologies.min", error.getMessage());
        })
        .verify();
  }
}
