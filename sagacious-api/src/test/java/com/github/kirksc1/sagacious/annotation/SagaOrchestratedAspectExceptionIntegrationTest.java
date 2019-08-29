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
public class SagaOrchestratedAspectExceptionIntegrationTest {

    @Autowired
    Orchestrator orchestrator;

    @Autowired
    TestSagaManager sagaManager;

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

    @AllArgsConstructor
    static class Orchestrator {

        private Participant participant;

        @SagaOrchestrated(failFor = IOException.class)
        public void orchestrateFailForIOException() throws IOException {
            participant.participateWithIOException();
        }

        @SagaOrchestrated(failForClassName = "IOException")
        public void orchestrateFailForClassNameIOException() throws IOException {
            participant.participateWithIOException();
        }

        @SagaOrchestrated(noFailFor = IllegalArgumentException.class)
        public void orchestrateNoFailForIllegalArgumentException() {
            participant.participateWithIllegalArgumentException();
        }

        @SagaOrchestrated(noFailForClassName = "IllegalArgumentException")
        public void orchestrateNoFailForClassNameIllegalArgumentException() {
            participant.participateWithIllegalArgumentException();
        }

        @SagaOrchestrated(failFor = RuntimeException.class, noFailFor = IllegalArgumentException.class)
        public void orchestrateNoFailForCloserMatchToIllegalArgumentException() {
            participant.participateWithIllegalArgumentException();
        }

        @SagaOrchestrated(noFailFor = RuntimeException.class, failFor = IllegalArgumentException.class)
        public void orchestrateFailForCloserMatchToIllegalArgumentException() {
            participant.participateWithIllegalArgumentException();
        }

    }

    static class Participant {
        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String participateWithIOException()throws IOException {
            throw new IOException();
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String participateWithIllegalArgumentException() {
            throw new IllegalArgumentException();
        }
    }

    @Test
    @Transactional
    public void testFailForIOException() {
        try {
            orchestrator.orchestrateFailForIOException();
        } catch (IOException e) {
            //expected
        }

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testOrchestrateFailForClassNameIOException() {
        try {
            orchestrator.orchestrateFailForClassNameIOException();
        } catch (IOException e) {
            //expected
        }

        assertEquals(true, sagaManager.createCalled);
        assertEquals(true, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testNoFailForIllegalArgumentException() {
        try {
            orchestrator.orchestrateNoFailForIllegalArgumentException();
        } catch (IllegalArgumentException e) {
            //expected
        }

        assertEquals(true, sagaManager.createCalled);
        assertEquals(false, sagaManager.failSagaCalled);
    }

    @Test
    @Transactional
    public void testNoFailForClassNameIllegalArgumentException() {
        try {
            orchestrator.orchestrateNoFailForClassNameIllegalArgumentException();
        } catch (IllegalArgumentException e) {
            //expected
        }

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
