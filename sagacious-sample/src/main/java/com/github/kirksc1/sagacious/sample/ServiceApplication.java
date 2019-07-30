package com.github.kirksc1.sagacious.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.*;
import com.github.kirksc1.sagacious.executor.RestTemplateExecutor;
import com.github.kirksc1.sagacious.strategy.SynchronousParticipantOrderStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan( basePackageClasses = {Saga.class} )
@EnableJpaRepositories({"com.github.kirksc1.sagacious"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    public SagaManager sagaManager(CrudRepository<Saga, String> repository, CompensatingActionStrategy compensatingActionStrategy, ObjectMapper objectMapper) {
        return new SimpleSagaManager(repository, compensatingActionStrategy, objectMapper);
    }

    @Bean
    public CompensatingActionStrategy compensatingActionStrategy(CrudRepository<Saga, String> repository, CompensatingActionExecutor executor, ObjectMapper objectMapper) {
        return new SynchronousParticipantOrderStrategy(repository, executor, objectMapper);
    }

    @Bean
    public CompensatingActionExecutor compensatingActionExecutor(RestTemplate restTemplate) {
        return new RestTemplateExecutor(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
