package com.github.kirksc1.sagacious.server;

import com.github.kirksc1.sagacious.SagaManager;
import com.github.kirksc1.sagacious.repository.SagaRepository;
import com.github.kirksc1.sagacious.server.valid.ValidConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ValidConfig.class)
public class ParticipantServerConfiguration {

    @Bean
    @ConditionalOnProperty(name = "sagacious.server.endpoints.add-participant.enabled", havingValue = "true", matchIfMissing = true)
    public AddParticipantController addParticipantController(SagaManager sagaManager, SagaRepository sagaRepository, SagaAssembler sagaAssembler, CompensatingActionDefinitionAssembler actionDefinitionAssembler) {
        return new AddParticipantController(sagaManager, sagaRepository, sagaAssembler, actionDefinitionAssembler);
    }

    @Bean
    public CompensatingActionDefinitionAssembler compensatingActionDefinitionAssembler() {
        return new CompensatingActionDefinitionAssembler();
    }

    @Bean
    public SagaAssembler sagaAssembler() {
        return new SagaAssembler();
    }

}
