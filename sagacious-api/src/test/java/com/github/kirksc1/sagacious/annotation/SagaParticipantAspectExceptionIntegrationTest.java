package com.github.kirksc1.sagacious.annotation;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.ParticipantIdentifier;
import com.github.kirksc1.sagacious.SagaIdentifier;
import com.github.kirksc1.sagacious.SagaManager;
import com.github.kirksc1.sagacious.action.CompensatingActionDefinitionFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SagaParticipantAspectExceptionIntegrationTest {


    @Autowired
    Orchestrator orchestrator;

    @Autowired
    TestSagaManager sagaManager;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
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
        public TestSagaManager sagaManager() {
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

    }

    @AllArgsConstructor
    static class Orchestrator {

        private Participant participant;

        @SagaOrchestrated
        public void orchestrateFailForIOException() {
            try {
                participant.participateWithFailForIOException();
            } catch (IOException e) {
                //ignore
            }
        }

        @SagaOrchestrated
        public void orchestrateFailForClassNameIOException() {
            try {
                participant.participateWithFailForClassNameIOException();
            } catch (IOException e) {
                //ignore
            }
        }

        @SagaOrchestrated
        public void orchestrateNoFailForIllegalArgumentException() {
            try {
                participant.participateWithNoFailForIllegalArgumentException();
            } catch (IllegalArgumentException e) {
                //ignore
            }
        }

        @SagaOrchestrated
        public void orchestrateNoFailForClassNameIllegalArgumentException() {
            try {
                participant.participateWithNoFailForClassNameIllegalArgumentException();
            } catch (IllegalArgumentException e) {
                //ignore
            }
        }

        @SagaOrchestrated
        public void orchestrateNoFailForCloserMatchToIllegalArgumentException() {
            try {
            participant.participateWithNoFailForCloserMatchToIllegalArgumentException();
            } catch (IllegalArgumentException e) {
                //ignore
            }
        }

        @SagaOrchestrated
        public void orchestrateFailForCloserMatchToIllegalArgumentException() {
            try {
            participant.participateWithFailForCloserMatchToIllegalArgumentException();
            } catch (IllegalArgumentException e) {
                //ignore
            }
        }

    }

    static class Participant {
        @SagaParticipant(actionDefinitionFactory = "testFactory", failFor = IOException.class)
        public String participateWithFailForIOException() throws IOException {
            throw new IOException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory", failForClassName = "IOException")
        public String participateWithFailForClassNameIOException() throws IOException {
            throw new IOException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory", noFailFor = IllegalArgumentException.class)
        public String participateWithNoFailForIllegalArgumentException() {
            throw new IllegalArgumentException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory", noFailForClassName = "IllegalArgumentException")
        public String participateWithNoFailForClassNameIllegalArgumentException() {
            throw new IllegalArgumentException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory", failFor = RuntimeException.class, noFailFor = IllegalArgumentException.class)
        public void participateWithNoFailForCloserMatchToIllegalArgumentException() {
            throw new IllegalArgumentException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory", noFailFor = RuntimeException.class, failFor = IllegalArgumentException.class)
        public void participateWithFailForCloserMatchToIllegalArgumentException() {
            throw new IllegalArgumentException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory",autoFail = false)
        public void participateWithNoAutoFailIllegalArgumentException() {
            throw new IllegalArgumentException();
        }
    }

    static class User {

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
        private boolean failSagaCalled = false;

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
            failSagaCalled = true;
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
            failSagaCalled = false;
        }
    }

    @After
    public void after() {
        sagaManager.reset();
    }

    @Test
    @Transactional
    public void testFailForIOException() {
        orchestrator.orchestrateFailForIOException();

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testOrchestrateFailForClassNameIOException() {
        orchestrator.orchestrateFailForClassNameIOException();

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testNoFailForIllegalArgumentException() {
        orchestrator.orchestrateNoFailForIllegalArgumentException();

        assertEquals(true, sagaManager.createCalled);
        assertEquals(false, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testNoFailForClassNameIllegalArgumentException() {
        orchestrator.orchestrateNoFailForClassNameIllegalArgumentException();

        assertEquals(true, sagaManager.createCalled);
        assertEquals(false, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testNoFailForCloserMatchToIllegalArgumentException() {
        try {
            orchestrator.orchestrateNoFailForCloserMatchToIllegalArgumentException();
        } catch (IllegalArgumentException e) {
            //expected
        }

        assertEquals(true, sagaManager.createCalled);
        assertEquals(false, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testFailForCloserMatchToIllegalArgumentException() {
        try {
            orchestrator.orchestrateFailForCloserMatchToIllegalArgumentException();
        } catch (IllegalArgumentException e) {
            //expected
        }

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.failSagaCalled);
    }
}
