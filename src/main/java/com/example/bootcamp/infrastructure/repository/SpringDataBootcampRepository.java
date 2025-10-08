package com.example.bootcamp.infrastructure.repository;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.BootcampPageRequest;
import com.example.bootcamp.domain.model.BootcampSummary;
import com.example.bootcamp.domain.model.CapabilitySummary;
import com.example.bootcamp.domain.model.PaginatedBootcamp;
import com.example.bootcamp.domain.model.TechnologySummary;
import com.example.bootcamp.infrastructure.mapper.BootcampMapper;
import com.example.bootcamp.infrastructure.repository.documents.BootcampEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.bootcamp.infrastructure.repository.support.BootcampRepositorySupport.BASE_SELECT;
import static com.example.bootcamp.infrastructure.repository.support.BootcampRepositorySupport.PAGINATED_SELECT_TEMPLATE;

import com.example.bootcamp.infrastructure.repository.support.BootcampRepositorySupport.BootcampCapabilityRow;
import com.example.bootcamp.infrastructure.repository.support.BootcampRepositorySupport.BootcampCapabilityTechnologyDetailRow;

@Repository
public class SpringDataBootcampRepository {

  private final R2dbcEntityTemplate template;

  public SpringDataBootcampRepository(R2dbcEntityTemplate template) {
    this.template = template;
  }

  public Mono<Bootcamp> findById(String id) {
    return findOneBy("id", id);
  }

  public Mono<Bootcamp> findByName(String name) {
    return findOneBy("name", name);
  }

  public Mono<Bootcamp> save(Bootcamp bootcamp) {
    var entity = BootcampMapper.toEntity(bootcamp);
    return template.insert(BootcampEntity.class)
        .using(entity)
        .then(insertBootcampCapabilities(bootcamp.id(), bootcamp.capabilities()))
        .thenReturn(bootcamp);
  }

  public Mono<Void> deleteById(String bootcampId) {
    return template.getDatabaseClient()
        .sql("CALL bootcamp.delete_bootcamp(:bootcampId)")
        .bind("bootcampId", bootcampId)
        .fetch()
        .rowsUpdated()
        .then();
  }

  public Mono<PaginatedBootcamp> findAll(BootcampPageRequest request) {
    String orderColumn = switch (request.sortBy()) {
      case NAME -> "name";
      case CAPABILITY_COUNT -> "capability_count";
    };
    String orderDirection = request.direction().name();
    String sql = String.format(PAGINATED_SELECT_TEMPLATE, orderColumn, orderDirection, "pb." + orderColumn, orderDirection);

    Mono<List<BootcampSummary>> bootcamps = template.getDatabaseClient()
        .sql(sql)
        .bind("limit", request.size())
        .bind("offset", request.page() * request.size())
        .map(this::mapPagedRow)
        .all()
        .transform(this::groupRowsByBootcamp)
        .collectList();

    return Mono.zip(bootcamps, countBootcamps())
        .map(tuple -> {
          List<BootcampSummary> content = tuple.getT1();
          long totalElements = tuple.getT2();
          int totalPages = (int) Math.ceil(totalElements / (double) request.size());
          return new PaginatedBootcamp(content, request.page(), request.size(), totalElements, totalPages);
        });
  }

  private Mono<Void> insertBootcampCapabilities(String bootcampId, List<String> capabilities) {
    return Flux.fromIterable(capabilities)
        .concatMap(capabilityId -> template.getDatabaseClient()
            .sql("INSERT INTO bootcamp.bootcamp_capability (bootcamp_id, capability_id) VALUES (:bootcampId, :capabilityId)")
            .bind("bootcampId", bootcampId)
            .bind("capabilityId", capabilityId)
            .fetch()
            .rowsUpdated())
        .then();
  }

  private Mono<Bootcamp> mapSingleResult(List<BootcampCapabilityRow> rows) {
    if (rows.isEmpty()) {
      return Mono.empty();
    }
    return Mono.fromCallable(() -> {
      var first = rows.get(0);
      var capabilities = rows.stream()
          .map(BootcampCapabilityRow::capabilityId)
          .filter(Objects::nonNull)
          .collect(Collectors.toCollection(LinkedHashSet::new));
      return BootcampMapper.toDomain(
          first.bootcampId(),
          first.bootcampName(),
          first.bootcampDescription(),
          first.launchDate(),
          first.durationWeeks(),
          List.copyOf(capabilities)
      );
    });
  }

  private Mono<Bootcamp> findOneBy(String column, String value) {
    return template.getDatabaseClient()
        .sql(BASE_SELECT + " WHERE b." + column + " = :value")
        .bind("value", value)
        .map(this::mapRow)
        .all()
        .collectList()
        .flatMap(this::mapSingleResult);
  }

  private BootcampCapabilityRow mapRow(Row row, RowMetadata metadata) {
    return new BootcampCapabilityRow(
        row.get("bootcamp_id", String.class),
        row.get("bootcamp_name", String.class),
        row.get("bootcamp_description", String.class),
        row.get("bootcamp_launch_date", LocalDate.class),
        row.get("bootcamp_duration_weeks", Integer.class),
        row.get("capability_id", String.class)
    );
  }

  private BootcampCapabilityTechnologyDetailRow mapPagedRow(Row row, RowMetadata metadata) {
    Number capabilityCount = row.get("capability_count", Number.class);
    return new BootcampCapabilityTechnologyDetailRow(
        row.get("bootcamp_id", String.class),
        row.get("bootcamp_name", String.class),
        row.get("bootcamp_description", String.class),
        row.get("bootcamp_launch_date", LocalDate.class),
        row.get("bootcamp_duration_weeks", Integer.class),
        capabilityCount == null ? 0 : capabilityCount.intValue(),
        row.get("capability_id", String.class),
        row.get("capability_name", String.class),
        row.get("capability_description", String.class),
        row.get("technology_id", String.class),
        row.get("technology_name", String.class)
    );
  }

  private Flux<BootcampSummary> groupRowsByBootcamp(Flux<BootcampCapabilityTechnologyDetailRow> rows) {
    return rows.groupBy(BootcampCapabilityTechnologyDetailRow::bootcampId)
        .concatMap(group -> group.collectList().map(this::mapToBootcampSummary));
  }

  private BootcampSummary mapToBootcampSummary(List<BootcampCapabilityTechnologyDetailRow> rows) {
    if (rows.isEmpty()) {
      throw new IllegalStateException("bootcamp.rows.empty");
    }
    var first = rows.get(0);
    Map<String, List<BootcampCapabilityTechnologyDetailRow>> capabilities = rows.stream()
        .filter(row -> row.capabilityId() != null)
        .collect(Collectors.groupingBy(
            BootcampCapabilityTechnologyDetailRow::capabilityId,
            LinkedHashMap::new,
            Collectors.toList()
        ));

    List<CapabilitySummary> capabilitySummaries = capabilities.values().stream()
        .map(this::mapToCapabilitySummary)
        .toList();

    Integer durationWeeks = first.durationWeeks();
    if (durationWeeks == null) {
      throw new IllegalStateException("bootcamp.duration.null");
    }

    return new BootcampSummary(
        first.bootcampId(),
        first.bootcampName(),
        first.bootcampDescription(),
        first.launchDate(),
        durationWeeks,
        capabilitySummaries,
        first.capabilityCount()
    );
  }

  private CapabilitySummary mapToCapabilitySummary(List<BootcampCapabilityTechnologyDetailRow> rows) {
    if (rows.isEmpty()) {
      throw new IllegalStateException("capability.rows.empty");
    }
    var first = rows.get(0);
    Map<String, TechnologySummary> technologies = rows.stream()
        .filter(row -> row.technologyId() != null && row.technologyName() != null)
        .collect(Collectors.toMap(
            BootcampCapabilityTechnologyDetailRow::technologyId,
            row -> new TechnologySummary(row.technologyId(), row.technologyName()),
            (existing, replacement) -> existing,
            LinkedHashMap::new
        ));
    return new CapabilitySummary(
        first.capabilityId(),
        first.capabilityName(),
        first.capabilityDescription(),
        List.copyOf(technologies.values()),
        technologies.size()
    );
  }

  private Mono<Long> countBootcamps() {
    return template.getDatabaseClient()
        .sql("SELECT COUNT(*) AS total FROM bootcamp.bootcamps")
        .map((row, metadata) -> {
          Number count = row.get("total", Number.class);
          return count == null ? 0L : count.longValue();
        })
        .one()
        .defaultIfEmpty(0L);
  }
}
