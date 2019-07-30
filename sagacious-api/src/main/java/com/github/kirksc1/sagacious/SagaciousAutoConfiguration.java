package com.github.kirksc1.sagacious;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaciousAutoConfiguration {

    @Bean
    @ConditionalOnBean(SagaManager.class)
    SagaOrchestratedAspect sagaOrchestratorAspect(SagaManager sagaManager) {
        return new SagaOrchestratedAspect(sagaManager);
    }

    @Bean
    @ConditionalOnBean(SagaManager.class)
    SagaParticipantAspect sagaParticipantAspect(SagaManager sagaManager, ApplicationContext context) {
        return new SagaParticipantAspect(sagaManager, context);
    }
}
