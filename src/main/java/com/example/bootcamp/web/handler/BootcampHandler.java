package com.example.bootcamp.web.handler;

import com.example.bootcamp.domain.error.DomainException;
import com.example.bootcamp.domain.model.BootcampPageRequest;
import com.example.bootcamp.domain.model.BootcampSortField;
import com.example.bootcamp.domain.model.PaginatedBootcamp;
import com.example.bootcamp.domain.model.BootcampSummary;
import com.example.bootcamp.domain.model.SortDirection;
import com.example.bootcamp.domain.model.TechnologySummary;
import com.example.bootcamp.domain.usecase.CreateBootcampUseCase;
import com.example.bootcamp.domain.usecase.DeleteBootcampUseCase;
import com.example.bootcamp.domain.usecase.ListBootcampUseCase;
import com.example.bootcamp.web.dto.Requests.*;
import com.example.bootcamp.web.dto.Responses.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class BootcampHandler {
  private final Validator validator;
  private final CreateBootcampUseCase createBootcamp;
  private final ListBootcampUseCase listBootcamp;
  private final DeleteBootcampUseCase deleteBootcampUseCase;

  public BootcampHandler(
      Validator validator,
      CreateBootcampUseCase createBootcamp,
      ListBootcampUseCase listBootcamp,
      DeleteBootcampUseCase deleteBootcamp
  ) {
    this.validator = validator;
    this.createBootcamp = createBootcamp;
    this.listBootcamp = listBootcamp;
    this.deleteBootcampUseCase = deleteBootcamp;
  }

  public Mono<ServerResponse> createBootcamp(ServerRequest req){
    return validatedBody(req, CreateBootcampRequest.class, body ->
      createBootcamp.execute(body.name(), body.description(), body.launchDate(), body.durationWeeks(), body.capabilities())
          .flatMap(f -> okJson(new IdResponse(f.id())))
    );
  }

  public Mono<ServerResponse> getAllBootcamp(ServerRequest req) {
    return Mono.justOrEmpty(parseRequest(req))
            .switchIfEmpty(Mono.error(new DomainException(com.example.bootcamp.domain.error.ErrorCodes.VALIDATION_ERROR, "invalid.pagination.parameters")))
            .flatMap(listBootcamp::execute)
            .flatMap(page -> okJson(Mapper.page(page)))
            .onErrorResume(DomainException.class, ex -> problem(mapHttp(ex.getCode()), ex.getMessage()));
  }

  public Mono<ServerResponse> deleteBootcamp(ServerRequest req) {
    String bootcampId = req.pathVariable("id");
    return deleteBootcampUseCase.execute(bootcampId)
        .then(ServerResponse.noContent().build())
        .onErrorResume(DomainException.class, ex -> problem(mapHttp(ex.getCode()), ex.getMessage()));
  }


  // helpers
  private static class Mapper {
    static BootcampPageResponse page(PaginatedBootcamp page) {
      return new BootcampPageResponse(
              page.content().stream().map(Mapper::bootcamp).toList(),
              page.page(),
              page.size(),
              page.totalElements(),
              page.totalPages()
      );
    }

    static BootcampResponse bootcamp(BootcampSummary summary){
      return new BootcampResponse(
        summary.id(),
        summary.name(),
        summary.description(),
        summary.launchDate(),
        summary.durationWeeks(),
        summary.capabilities().stream().map(Mapper::capability).toList(),
        summary.capabilityCount()
      );
    }

    static CapabilityResponse capability(com.example.bootcamp.domain.model.CapabilitySummary capability) {
      return new CapabilityResponse(
              capability.id(),
              capability.name(),
              capability.description(),
              capability.technologies().stream().map(Mapper::technology).toList(),
              capability.technologyCount()
      );
    }

    static TechnologyResponse technology(TechnologySummary technology) {
      return new TechnologyResponse(technology.id(), technology.name());
    }
  }

  private java.util.Optional<BootcampPageRequest> parseRequest(ServerRequest req) {
    try {
      int page = req.queryParam("page").map(Integer::parseInt).orElse(0);
      int size = req.queryParam("size").map(Integer::parseInt).orElse(10);
      BootcampSortField sortField = req.queryParam("sortBy")
              .map(String::toUpperCase)
              .map(value -> switch (value) {
                case "NAME" -> BootcampSortField.NAME;
                case "TECHNOLOGY_COUNT", "TECHNOLOGIES", "CAPABILITY_COUNT", "CAPABILITIES" -> BootcampSortField.CAPABILITY_COUNT;
                default -> throw new IllegalArgumentException("invalid.sort.by");
              })
              .orElse(BootcampSortField.NAME);
      SortDirection direction = req.queryParam("order")
              .map(String::toUpperCase)
              .map(SortDirection::valueOf)
              .orElse(SortDirection.ASC);
      BootcampPageRequest request = new BootcampPageRequest(page, size, sortField, direction);
      return java.util.Optional.of(request);
    } catch (IllegalArgumentException ex) {
      return java.util.Optional.empty();
    }
  }

  private <T> Mono<ServerResponse> validatedBody(ServerRequest req, Class<T> clazz, java.util.function.Function<T, Mono<ServerResponse>> fn){
    return req.bodyToMono(clazz).flatMap(body -> {
      var errors = new BeanPropertyBindingResult(body, clazz.getSimpleName());
      validator.validate(body, errors);
      if (errors.hasErrors()) {
        String msg = errors.getAllErrors().stream().map(e -> e.getDefaultMessage()==null?"validation.error":e.getDefaultMessage()).reduce((a,b) -> a+","+b).orElse("validation.error");
        return problem(400, msg);
      }
      return fn.apply(body);
    }).onErrorResume(DomainException.class, ex -> problem(mapHttp(ex.getCode()), ex.getMessage()))
        .onErrorResume(IllegalArgumentException.class, ex -> problem(400, ex.getMessage()));
  }

  private Mono<ServerResponse> okJson(Object any){
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(any));
  }
  private Mono<ServerResponse> problem(int status, String message){
    return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).body(fromValue(java.util.Map.of("message", message)));
  }
  private int mapHttp(com.example.bootcamp.domain.error.ErrorCodes code){
    return switch (code){
      case TECHNOLOGY_NOT_FOUND, BRANCH_NOT_FOUND, PRODUCT_NOT_FOUND -> 404;
      case BOOTCAMP_NOT_FOUND -> 404;
      case VALIDATION_ERROR -> 400;
      case CONFLICT -> 409;
      default -> 500;
    };
  }
}
