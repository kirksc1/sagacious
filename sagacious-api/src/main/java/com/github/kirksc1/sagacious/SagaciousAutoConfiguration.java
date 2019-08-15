package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.executor.RestTemplateExecutor;
import com.github.kirksc1.sagacious.identifier.UuidFactory;
import com.github.kirksc1.sagacious.strategy.SynchronousParticipantOrderStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class SagaciousAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "sagaOrchestratorAspect")
    public SagaOrchestratedAspect sagaOrchestratorAspect(ApplicationContext context) {
        return new SagaOrchestratedAspect(context);
    }

    @Bean
    @ConditionalOnMissingBean(name = "sagaParticipantAspect")
    public SagaParticipantAspect sagaParticipantAspect(ApplicationContext context) {
        return new SagaParticipantAspect(context);
    }

    @Bean
    @ConditionalOnMissingBean(name = "sagaManager")
    public SagaManager sagaManager(CrudRepository<Saga, String> repository, CompensatingActionStrategy compensatingActionStrategy, ObjectMapper objectMapper) {
        return new SimpleSagaManager(repository, compensatingActionStrategy, objectMapper);
    }

    @Bean
    public CompensatingActionStrategy compensatingActionStrategy(CrudRepository<Saga, String> repository, CompensatingActionManager manager, ObjectMapper objectMapper) {
        return new SynchronousParticipantOrderStrategy(repository, manager, objectMapper);
    }

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public CompensatingActionExecutor restCompensatingActionExecutor(RestTemplate restTemplate) {
        return new RestTemplateExecutor(restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public CompensatingActionDefinitionMatcher compensatingActionDefinitionMatcher() {
        return new SimpleCompensatingActionDefinitionMatcher();
    }

    @Bean
    @ConditionalOnMissingBean
    public CompensatingActionManager compensatingActionManager(CompensatingActionDefinitionMatcher matcher, List<CompensatingActionExecutor> executors) {
        return new CompensatingActionManager(matcher, executors);
    }

    @Bean
    @ConditionalOnMissingBean(name = "sagaIdentifierFactory")
    public IdentifierFactory sagaIdentifierFactory() {
        return new UuidFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "participantIdentifierFactory")
    public IdentifierFactory participantIdentifierFactory() {
        return new UuidFactory();
    }
}
