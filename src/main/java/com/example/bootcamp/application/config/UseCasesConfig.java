package com.example.bootcamp.application.config;

import com.example.bootcamp.domain.usecase.CreateBootcampUseCase;
import com.example.bootcamp.domain.usecase.DeleteBootcampUseCase;
import com.example.bootcamp.domain.usecase.ListBootcampUseCase;
import com.example.bootcamp.infrastructure.client.BootcampReportClient;
import com.example.bootcamp.infrastructure.repository.SpringDataBootcampRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public CreateBootcampUseCase createBootcampUseCase(SpringDataBootcampRepository repo, BootcampReportClient client) {
        return new CreateBootcampUseCase(repo, client);
    }

    @Bean
    public ListBootcampUseCase listBootcampUseCase(SpringDataBootcampRepository repo) {
        return new ListBootcampUseCase(repo);
    }

    @Bean
    public DeleteBootcampUseCase deleteBootcampUseCase(SpringDataBootcampRepository repo) {
        return new DeleteBootcampUseCase(repo);
    }
}

