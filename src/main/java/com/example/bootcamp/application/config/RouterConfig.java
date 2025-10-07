package com.example.bootcamp.application.config;

import com.example.bootcamp.web.handler.BootcampHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
  @Bean
  public RouterFunction<ServerResponse> routes(BootcampHandler h) {
    return RouterFunctions.nest(RequestPredicates.path("/api/v1"),
      RouterFunctions.route()
        .POST("/bootcamp", h::createBootcamp)
        .GET("/bootcamp", h::getAllBootcamp)
        .build()
    );
  }
}
