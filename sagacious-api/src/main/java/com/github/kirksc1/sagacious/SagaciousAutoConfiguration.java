package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.executor.RestTemplateExecutor;
import com.github.kirksc1.sagacious.strategy.SynchronousParticipantOrderStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SagaciousAutoConfiguration {

    @Bean
    public SagaOrchestratedAspect sagaOrchestratorAspect(SagaManager sagaManager) {
        return new SagaOrchestratedAspect(sagaManager);
    }

    @Bean
    public SagaParticipantAspect sagaParticipantAspect(SagaManager sagaManager, ApplicationContext context) {
        return new SagaParticipantAspect(sagaManager, context);
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
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
