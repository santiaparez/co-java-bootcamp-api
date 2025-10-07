package com.example.bootcamp.web;

import com.example.bootcamp.application.config.RouterConfig;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.usecase.*;
import com.example.bootcamp.web.dto.Requests.*;
import com.example.bootcamp.web.handler.BootcampHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

class BootcampHandlerTest {

  private final CreateBootcampUseCase create = Mockito.mock(CreateBootcampUseCase.class);
  private final ListBootcampUseCase getAll = Mockito.mock(ListBootcampUseCase.class);
  private final DeleteBootcampUseCase delete = Mockito.mock(DeleteBootcampUseCase.class);

  private WebTestClient client;

  @BeforeEach
  void setUp() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    BootcampHandler handler = new BootcampHandler(
        validator,
        create,
        getAll,
        delete
    );
    client = WebTestClient.bindToRouterFunction(new RouterConfig().routes(handler))
        .handlerStrategies(HandlerStrategies.withDefaults())
        .configureClient()
        .baseUrl("/api/v1")
        .build();
  }

  @Test
  void createTech_success() {
    Mockito.when(create.execute("Acme","des", LocalDate.EPOCH, 2, java.util.List.of("t1","t2","t3")))
        .thenReturn(Mono.just(new Bootcamp("id-1", "Acme", "des", LocalDate.EPOCH, 2,java.util.List.of("t1","t2","t3"))));

    client.post().uri("/bootcamp")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new CreateBootcampRequest("Acme","des", LocalDate.EPOCH, 2,java.util.List.of("t1","t2","t3")))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo("id-1");
  }

  @Test
  void createTech_validationError() {
    client.post().uri("/bootcamp")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new CreateBootcampRequest("","", LocalDate.EPOCH, 2,java.util.List.of("t1","t2","t3")))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.message").isEqualTo("no debe estar vacío,no debe estar vacío");
  }

}

