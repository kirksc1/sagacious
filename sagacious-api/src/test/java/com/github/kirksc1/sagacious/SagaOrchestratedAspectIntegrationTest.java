package com.github.kirksc1.sagacious;

import lombok.AllArgsConstructor;
import lombok.Data;
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
                    return null;
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
    }

    @AllArgsConstructor
    static class Orchestrator {

        private Participant participant;

        @SagaOrchestrated
        public void orchestrate() {
            participant.participate();
        }

        @SagaOrchestrated(sagaManager = "testSagaManager")
        public void customizedOrchestrate() {
            participant.participate();
        }
    }

    static class Participant {
        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String participate() {
            return null;
        }
    }

    @Data
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
    }

    @Test
    public void testOverriddenSagaManagerBean() {
        orchestrator.orchestrate();

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.addParticipantCalled);
        assertEquals(true, sagaManager.completeCalled);

        assertEquals(false, testSagaManager.createCalled);
    }

    @Test
    public void testCustomSagaManagerBean() {
        orchestrator.customizedOrchestrate();

        assertEquals(true, testSagaManager.createCalled);
        assertEquals(true, testSagaManager.addParticipantCalled);
        assertEquals(true, testSagaManager.completeCalled);

        assertEquals(false, sagaManager.createCalled);
    }
}
