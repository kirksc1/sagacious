package com.github.kirksc1.sagacious;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SagaParticipantAspectIntegrationTest {

    @Autowired
    @Qualifier("participantIdentifierFactory")
    TestIdentifierFactory identifierFactory;

    @Autowired
    @Qualifier("testIdentifierFactory")
    TestIdentifierFactory testIdentifierFactory;

    @Autowired
    Orchestrator orchestrator;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {

        @Bean
        public CompensatingActionDefinitionFactory<String> testFactory() {
            return new CompensatingActionDefinitionFactory<String>() {
                @Override
                public CompensatingActionDefinition buildDefinition(String item) {
                    return new CompensatingActionDefinition();
                }
            };
        }

        @Bean
        public Orchestrator orchestrator(Participant participant) {
            return new Orchestrator(participant);
        }

        @Bean
        public Participant participant() {
            return new Participant();
        }

        @Bean
        public TestIdentifierFactory participantIdentifierFactory() {
            return new TestIdentifierFactory();
        }

        @Bean
        public TestIdentifierFactory testIdentifierFactory() {
            return new TestIdentifierFactory();
        }
    }

    @AllArgsConstructor
    static class Orchestrator {

        private Participant participant;

        @SagaOrchestrated
        public void overriddenParticipantIdentifierFactoryOrchestrate() {
            participant.overriddenIdentifierFactoryParticipate();
        }

        @SagaOrchestrated
        public void customizedParticipantIdentifierFactoryOrchestrate() {
            participant.customizedIdentifierFactoryParticipate();
        }

    }

    static class Participant {
        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String overriddenIdentifierFactoryParticipate() {
            return null;
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory", identifierFactory = "testIdentifierFactory")
        public String customizedIdentifierFactoryParticipate() {
            return null;
        }
    }

    @Getter
    static class TestIdentifierFactory implements IdentifierFactory {
        private boolean buildCalled = false;

        @Override
        public String buildIdentifier() {
            buildCalled = true;
            return "";
        }

        public void reset() {
            buildCalled = false;
        }
    }

    @After
    public void after() {
        identifierFactory.reset();
        testIdentifierFactory.reset();
    }

    @Test
    @Transactional
    public void testOverriddenIdentifierFactoryBean() {
        orchestrator.overriddenParticipantIdentifierFactoryOrchestrate();

        assertEquals(true, identifierFactory.buildCalled);

        assertEquals(false, testIdentifierFactory.buildCalled);
    }

    @Test
    @Transactional
    public void testCustomizedIdentifierFactoryBean() {
        orchestrator.customizedParticipantIdentifierFactoryOrchestrate();

        assertEquals(true, testIdentifierFactory.buildCalled);

        assertEquals(false, identifierFactory.buildCalled);
    }
}
