package com.example.bootcamp.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info()
      .title("Bootcamp API")
      .version("1.0.0")
      .description("Reactive API to manage Technologies.")
      .contact(new Contact().name("API Team").email("api@example.com"))
    );
  }
}
