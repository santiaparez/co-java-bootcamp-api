package com.example.bootcamp.infrastructure.repository;

import com.example.bootcamp.domain.model.BootcampPageRequest;
import com.example.bootcamp.domain.model.BootcampSortField;
import com.example.bootcamp.domain.model.BootcampSummary;
import com.example.bootcamp.domain.model.CapabilitySummary;
import com.example.bootcamp.domain.model.SortDirection;
import com.example.bootcamp.domain.model.TechnologySummary;
import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringDataBootcampRepositoryTest {

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"})
  void findAllAggregatesBootcampWithCapabilitiesAndTechnologies() throws Exception {
    R2dbcEntityTemplate template = mock(R2dbcEntityTemplate.class);
    DatabaseClient databaseClient = mock(DatabaseClient.class);
    DatabaseClient.GenericExecuteSpec pagedSpec = mock(DatabaseClient.GenericExecuteSpec.class);
    DatabaseClient.TypedExecuteSpec pagedTypedSpec = mock(DatabaseClient.TypedExecuteSpec.class);
    DatabaseClient.GenericExecuteSpec countSpec = mock(DatabaseClient.GenericExecuteSpec.class);
    DatabaseClient.TypedExecuteSpec countTypedSpec = mock(DatabaseClient.TypedExecuteSpec.class);

    when(template.getDatabaseClient()).thenReturn(databaseClient);

    when(databaseClient.sql(argThat(sql -> sql.contains("WITH bootcamp_counts")))).thenReturn(pagedSpec);
    when(databaseClient.sql("SELECT COUNT(*) AS total FROM bootcamp.bootcamps")).thenReturn(countSpec);

    when(pagedSpec.bind(eq("limit"), any())).thenReturn(pagedSpec);
    when(pagedSpec.bind(eq("offset"), any())).thenReturn(pagedSpec);
    when(pagedSpec.map(any())).thenReturn(pagedTypedSpec);

    Class<?> detailClass = Class.forName("com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository$BootcampCapabilityTechnologyDetailRow");
    Constructor<?> constructor = detailClass.getDeclaredConstructor(
        String.class,
        String.class,
        String.class,
        LocalDate.class,
        Integer.class,
        int.class,
        String.class,
        String.class,
        String.class,
        String.class,
        String.class
    );
    constructor.setAccessible(true);

    LocalDate launchDate = LocalDate.of(2024, 1, 15);

    Object row1 = constructor.newInstance(
        "bootcamp-1",
        "Bootcamp 1",
        "A description",
        launchDate,
        12,
        2,
        "cap-1",
        "Capability 1",
        "Capability 1 description",
        "tech-1",
        "Tech 1"
    );
    Object row2 = constructor.newInstance(
        "bootcamp-1",
        "Bootcamp 1",
        "A description",
        launchDate,
        12,
        2,
        "cap-1",
        "Capability 1",
        "Capability 1 description",
        "tech-2",
        "Tech 2"
    );
    Object row3 = constructor.newInstance(
        "bootcamp-1",
        "Bootcamp 1",
        "A description",
        launchDate,
        12,
        2,
        "cap-2",
        "Capability 2",
        "Capability 2 description",
        null,
        null
    );

    Flux rowFlux = Flux.fromIterable(List.of(row1, row2, row3));
    when(pagedTypedSpec.all()).thenReturn(rowFlux);

    when(countSpec.map(any())).thenReturn(countTypedSpec);
    when(countTypedSpec.one()).thenReturn(Mono.just(1L));

    SpringDataBootcampRepository repository = new SpringDataBootcampRepository(template);
    BootcampPageRequest request = new BootcampPageRequest(0, 10, BootcampSortField.NAME, SortDirection.ASC);

    StepVerifier.create(repository.findAll(request))
        .assertNext(page -> {
          assertEquals(0, page.page());
          assertEquals(10, page.size());
          assertEquals(1L, page.totalElements());
          assertEquals(1, page.totalPages());

          assertEquals(1, page.content().size());

          BootcampSummary summary = page.content().get(0);
          assertEquals("bootcamp-1", summary.id());
          assertEquals("Bootcamp 1", summary.name());
          assertEquals("A description", summary.description());
          assertEquals(launchDate, summary.launchDate());
          assertEquals(12, summary.durationWeeks());
          assertEquals(2, summary.capabilityCount());

          List<CapabilitySummary> capabilities = summary.capabilities();
          assertEquals(2, capabilities.size());

          CapabilitySummary capability1 = capabilities.get(0);
          assertEquals("cap-1", capability1.id());
          assertEquals("Capability 1", capability1.name());
          assertEquals("Capability 1 description", capability1.description());
          assertEquals(2, capability1.technologyCount());
          assertEquals(
              List.of(
                  new TechnologySummary("tech-1", "Tech 1"),
                  new TechnologySummary("tech-2", "Tech 2")
              ),
              capability1.technologies()
          );

          CapabilitySummary capability2 = capabilities.get(1);
          assertEquals("cap-2", capability2.id());
          assertEquals("Capability 2", capability2.name());
          assertEquals("Capability 2 description", capability2.description());
          assertEquals(0, capability2.technologyCount());
          assertEquals(List.of(), capability2.technologies());
        })
        .verifyComplete();
  }
}
