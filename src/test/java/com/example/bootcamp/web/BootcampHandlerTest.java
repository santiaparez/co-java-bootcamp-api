package com.example.bootcamp.web;

import com.example.bootcamp.application.config.RouterConfig;
import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.error.ErrorCodes;
import com.example.bootcamp.domain.model.*;
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
import java.util.List;

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

  @Test
  void getAllBootcamp_success() {
    TechnologySummary technology = new TechnologySummary("tech-1", "Java");
    CapabilitySummary capability = new CapabilitySummary("cap-1", "Backend", "desc", List.of(technology), 1);
    BootcampSummary bootcamp = new BootcampSummary("boot-1", "Bootcamp", "desc", LocalDate.EPOCH, 6, List.of(capability), 1);
    PaginatedBootcamp page = new PaginatedBootcamp(List.of(bootcamp), 0, 10, 1, 1);

    Mockito.when(getAll.execute(Mockito.any())).thenReturn(Mono.just(page));

    client.get().uri("/bootcamp")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.page").isEqualTo(0)
        .jsonPath("$.size").isEqualTo(10)
        .jsonPath("$.totalElements").isEqualTo(1)
        .jsonPath("$.totalPages").isEqualTo(1)
        .jsonPath("$.content[0].id").isEqualTo("boot-1")
        .jsonPath("$.content[0].capabilities[0].technologies[0].name").isEqualTo("Java");

    Mockito.verify(getAll).execute(new BootcampPageRequest(0, 10, BootcampSortField.NAME, SortDirection.ASC));
  }

  @Test
  void getAllBootcamp_invalidSortOrder() {
    client.get().uri(uriBuilder -> uriBuilder.path("/bootcamp").queryParam("order", "descending").build())
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.message").isEqualTo("invalid.sort.order");

    Mockito.verifyNoInteractions(getAll);
  }

  @Test
  void deleteBootcamp_success() {
    Mockito.when(delete.execute("id-1")).thenReturn(Mono.empty());

    client.delete().uri("/bootcamp/{id}", "id-1")
        .exchange()
        .expectStatus().isNoContent();

    Mockito.verify(delete).execute("id-1");
  }

  @Test
  void deleteBootcamp_notFound() {
    Mockito.when(delete.execute("missing"))
        .thenReturn(Mono.error(new DomainException(ErrorCodes.BOOTCAMP_NOT_FOUND, "bootcamp.not.found")));

    client.delete().uri("/bootcamp/{id}", "missing")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.message").isEqualTo("bootcamp.not.found");
  }

}

