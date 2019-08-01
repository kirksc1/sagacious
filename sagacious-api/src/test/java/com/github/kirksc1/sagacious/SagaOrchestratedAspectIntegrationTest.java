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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SagaOrchestratedAspectIntegrationTest {

    @Autowired
    @Qualifier("sagaManager")
    TestSagaManager sagaManager;

    @Autowired
    @Qualifier("testSagaManager")
    TestSagaManager testSagaManager;

    @Autowired
    @Qualifier("sagaIdentifierFactory")
    TestIdentifierFactory identifierFactory;

    @Autowired
    @Qualifier("testIdentifierFactory")
    TestIdentifierFactory testIdentifierFactory;

    @Autowired
    Orchestrator orchestrator;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {
        @Bean
        public TestSagaManager sagaManager() {
            return new TestSagaManager();
        }

        @Bean
        public TestSagaManager testSagaManager() {
            return new TestSagaManager();
        }

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
        public TestIdentifierFactory sagaIdentifierFactory() {
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
        public void overriddenSagaManagerOrchestrate() {
            participant.participate();
        }

        @SagaOrchestrated(sagaManager = "testSagaManager")
        public void customizedSagaManagerOrchestrate() {
            participant.participate();
        }

        @SagaOrchestrated
        public void overriddenIdentifierFactoryOrchestrate() {
            participant.participate();
        }

        @SagaOrchestrated(identifierFactory = "testIdentifierFactory")
        public void customizedIdentifierFactoryOrchestrate() {
            participant.participate();
        }
    }

    static class Participant {
        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String participate() {
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

    @Getter
    static class TestSagaManager implements SagaManager {
        private boolean createCalled = false;
        private boolean addParticipantCalled = false;
        private boolean completeCalled = false;

        @Override
        public boolean createSaga(SagaIdentifier sagaIdentifier) {
            createCalled = true;
            return true;
        }

        @Override
        public boolean addParticipant(SagaIdentifier sagaIdentifier, ParticipantIdentifier participantIdentifier, CompensatingActionDefinition compensatingAction) {
            addParticipantCalled = true;
            return false;
        }

        @Override
        public boolean failSaga(SagaIdentifier sagaIdentifier) {
            return false;
        }

        @Override
        public boolean completeSaga(SagaIdentifier sagaIdentifier) {
            completeCalled = true;
            return false;
        }

        @Override
        public boolean hasSagaFailed(SagaIdentifier sagaIdentifier) {
            return false;
        }

        @Override
        public boolean hasSagaCompleted(SagaIdentifier sagaIdentifier) {
            return false;
        }

        public void reset() {
            createCalled = false;
            addParticipantCalled = false;
            completeCalled = false;
        }
    }

    @After
    public void after() {
        sagaManager.reset();
        testSagaManager.reset();

        identifierFactory.reset();
        testIdentifierFactory.reset();
    }

    @Test
    public void testOverriddenSagaManagerBean() {
        orchestrator.overriddenSagaManagerOrchestrate();

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.addParticipantCalled);
        assertEquals(true, sagaManager.completeCalled);

        assertEquals(false, testSagaManager.createCalled);
    }

    @Test
    public void testCustomizedSagaManagerBean() {
        orchestrator.customizedSagaManagerOrchestrate();

        assertEquals(true, testSagaManager.createCalled);
        assertEquals(true, testSagaManager.addParticipantCalled);
        assertEquals(true, testSagaManager.completeCalled);

        assertEquals(false, sagaManager.createCalled);
    }

    @Test
    public void testOverriddenIdentifierFactoryBean() {
        orchestrator.overriddenIdentifierFactoryOrchestrate();

        assertEquals(true, identifierFactory.buildCalled);

        assertEquals(false, testIdentifierFactory.buildCalled);
    }

    @Test
    public void testCustomizedIdentifierFactoryBean() {
        orchestrator.customizedIdentifierFactoryOrchestrate();

        assertEquals(true, testIdentifierFactory.buildCalled);

        assertEquals(false, identifierFactory.buildCalled);
    }
}