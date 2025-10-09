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

import static com.example.bootcamp.infrastructure.repository.support.BootcampRepositorySupport.*;

@Repository
public class SpringDataBootcampRepository {

  private final R2dbcEntityTemplate template;

  public SpringDataBootcampRepository(R2dbcEntityTemplate template) {
    this.template = template;
  }

  public Mono<Bootcamp> findById(String id) {
    return findOneBy(BOOTCAMP_COLUMN_ID, id);
  }

  public Mono<Bootcamp> findByName(String name) {
    return findOneBy(BOOTCAMP_COLUMN_NAME, name);
  }

  public Mono<Bootcamp> save(Bootcamp bootcamp) {
    var entity = BootcampMapper.toEntity(bootcamp);
    return template.insert(BootcampEntity.class)
        .using(entity)
        .then(insertBootcampCapabilities(bootcamp.id(), bootcamp.capabilities()))
        .thenReturn(bootcamp);
  }

  public Mono<BootcampSummary> findSummaryById(String bootcampId) {
    return template.getDatabaseClient()
        .sql(SELECT_BOOTCAMP_DETAILS_BY_ID)
        .bind(PARAM_BOOTCAMP_ID, bootcampId)
        .map(this::mapDetailRow)
        .all()
        .collectList()
        .flatMap(rows -> {
          if (rows.isEmpty()) {
            return Mono.empty();
          }
          return Mono.fromCallable(() -> mapToBootcampSummary(rows));
        });
  }

  public Mono<Void> deleteById(String bootcampId) {
    return template.getDatabaseClient()
        .sql(DELETE_BOOTCAMP_PROCEDURE)
        .bind(PARAM_BOOTCAMP_ID, bootcampId)
        .fetch()
        .rowsUpdated()
        .then();
  }

  public Mono<PaginatedBootcamp> findAll(BootcampPageRequest request) {
    String orderColumn = switch (request.sortBy()) {
      case NAME -> BOOTCAMP_COLUMN_NAME;
      case CAPABILITY_COUNT -> COLUMN_CAPABILITY_COUNT;
    };
    String orderDirection = request.direction().name();
    String sql = String.format(
        PAGINATED_SELECT_TEMPLATE,
        orderColumn,
        orderDirection,
        PAGED_BOOTCAMP_ALIAS_PREFIX + orderColumn,
        orderDirection
    );

    Mono<List<BootcampSummary>> bootcamps = template.getDatabaseClient()
        .sql(sql)
        .bind(PARAM_LIMIT, request.size())
        .bind(PARAM_OFFSET, request.page() * request.size())
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
            .sql(INSERT_BOOTCAMP_CAPABILITY)
            .bind(PARAM_BOOTCAMP_ID, bootcampId)
            .bind(PARAM_CAPABILITY_ID, capabilityId)
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
        .sql(BASE_SELECT + " WHERE " + BOOTCAMP_ALIAS_PREFIX + column + " = :" + PARAM_VALUE)
        .bind(PARAM_VALUE, value)
        .map(this::mapRow)
        .all()
        .collectList()
        .flatMap(this::mapSingleResult);
  }

  private BootcampCapabilityRow mapRow(Row row, RowMetadata metadata) {
    return new BootcampCapabilityRow(
        row.get(COLUMN_BOOTCAMP_ID, String.class),
        row.get(COLUMN_BOOTCAMP_NAME, String.class),
        row.get(COLUMN_BOOTCAMP_DESCRIPTION, String.class),
        row.get(COLUMN_BOOTCAMP_LAUNCH_DATE, LocalDate.class),
        row.get(COLUMN_BOOTCAMP_DURATION_WEEKS, Integer.class),
        row.get(COLUMN_CAPABILITY_ID, String.class)
    );
  }

  private BootcampCapabilityTechnologyDetailRow mapPagedRow(Row row, RowMetadata metadata) {
    Number capabilityCount = row.get(COLUMN_CAPABILITY_COUNT, Number.class);
    return new BootcampCapabilityTechnologyDetailRow(
        row.get(COLUMN_BOOTCAMP_ID, String.class),
        row.get(COLUMN_BOOTCAMP_NAME, String.class),
        row.get(COLUMN_BOOTCAMP_DESCRIPTION, String.class),
        row.get(COLUMN_BOOTCAMP_LAUNCH_DATE, LocalDate.class),
        row.get(COLUMN_BOOTCAMP_DURATION_WEEKS, Integer.class),
        capabilityCount == null ? 0 : capabilityCount.intValue(),
        row.get(COLUMN_CAPABILITY_ID, String.class),
        row.get(COLUMN_CAPABILITY_NAME, String.class),
        row.get(COLUMN_CAPABILITY_DESCRIPTION, String.class),
        row.get(COLUMN_TECHNOLOGY_ID, String.class),
        row.get(COLUMN_TECHNOLOGY_NAME, String.class)
    );
  }

  private BootcampCapabilityTechnologyDetailRow mapDetailRow(Row row, RowMetadata metadata) {
    return new BootcampCapabilityTechnologyDetailRow(
        row.get(COLUMN_BOOTCAMP_ID, String.class),
        row.get(COLUMN_BOOTCAMP_NAME, String.class),
        row.get(COLUMN_BOOTCAMP_DESCRIPTION, String.class),
        row.get(COLUMN_BOOTCAMP_LAUNCH_DATE, LocalDate.class),
        row.get(COLUMN_BOOTCAMP_DURATION_WEEKS, Integer.class),
        0,
        row.get(COLUMN_CAPABILITY_ID, String.class),
        row.get(COLUMN_CAPABILITY_NAME, String.class),
        row.get(COLUMN_CAPABILITY_DESCRIPTION, String.class),
        row.get(COLUMN_TECHNOLOGY_ID, String.class),
        row.get(COLUMN_TECHNOLOGY_NAME, String.class)
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

    int capabilityCount = first.capabilityCount();
    if (capabilityCount == 0) {
      capabilityCount = capabilitySummaries.size();
    }

    return new BootcampSummary(
        first.bootcampId(),
        first.bootcampName(),
        first.bootcampDescription(),
        first.launchDate(),
        durationWeeks,
        capabilitySummaries,
        capabilityCount
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
        .sql(COUNT_BOOTCAMPS_QUERY)
        .map((row, metadata) -> {
          Number count = row.get(COUNT_TOTAL_ALIAS, Number.class);
          return count == null ? 0L : count.longValue();
        })
        .one()
        .defaultIfEmpty(0L);
  }
}
