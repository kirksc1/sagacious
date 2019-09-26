package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.action.*;
import com.github.kirksc1.sagacious.annotation.Executable;
import com.github.kirksc1.sagacious.repository.Participant;
import com.github.kirksc1.sagacious.repository.Saga;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SimpleSagaManagerNotCompletedFailedIntegrationTest {

    @Autowired
    SagaManager sagaManager;

    @Autowired
    JpaRepository<Saga, String> sagaRepository;

    @Autowired
    AlternatingFailureExecutor executor;

    @Before
    public void before() {
        executor.reset();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public SagaManager sagaManager(CrudRepository<Saga, String> repository, CompensatingActionStrategy strategy, ObjectMapper objectMapper) {
            return new SimpleSagaManager(repository, strategy, objectMapper);
        }

        @Bean
        public CompensatingActionStrategy compensatingActionStrategy(CrudRepository<Saga, String> repository, CompensatingActionManager manager, ObjectMapper objectMapper) {
            return new SynchronousParticipantOrderStrategy(repository, manager, objectMapper);
        }

        @Bean
        public CompensatingActionExecutor compensatingActionExecutor() {
            return new AlternatingFailureExecutor();
        }

        @Bean
        public CompensatingActionManager compensatingActionManager(List<CompensatingActionExecutor> executorList) {
            return new CompensatingActionManager(new SimpleCompensatingActionDefinitionMatcher(), executorList);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Executable
    static class AlternatingFailureExecutor implements CompensatingActionExecutor {
        private boolean fail = false;
        private int calledCount = 0;
        @Override
        public boolean execute(CompensatingActionDefinition definition) {
            calledCount++;
            fail = !fail;
            System.out.println("CompensatingAction success=" + fail);
            return fail;
        }

        public void reset() {
            fail = false;
            calledCount = 0;
        }

        public int getCalledCount() {
            return calledCount;
        }
    }

    @Test
    public void testAddParticipant_whenDetailsProvidedAndSagaFailedNotCompleted_thenParticipantAdded() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("failed-notcompleted"), new ParticipantIdentifier("test"), definition);

        assertEquals(false, retVal);

        Optional<Saga> sagaOpt = sagaRepository.findById("failed-notcompleted");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals(2, saga.getParticipants().size());

        Participant participant = saga.getParticipants().get(1);
        assertEquals("test", participant.getIdentifier());
        assertEquals(false, participant.getFailCompleted());
    }

    @Test
    public void testAddParticipant_whenDetailsProvidedAndSagaFailedNotCompleted_thenActionExecuted() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("failed-notcompleted"), new ParticipantIdentifier("test"), definition);

        assertEquals(2, executor.getCalledCount());
    }

    @Test
    public void testFailSaga_whenFailedSaga_thenCompleteCompensatingActions() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("failed-notcompleted");
        boolean retVal = sagaManager.failSaga(sagaIdentifier);

        assertEquals(true, retVal);
        Optional<Saga> sagaOpt = sagaRepository.findById("failed-notcompleted");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.TRUE, saga.isCompleted());

        assertEquals(1, executor.calledCount);
    }

    @Test
    public void testCompleteSaga_whenFailedSaga_thenSagaStateNotChanged() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("failed-notcompleted");
        boolean retVal = sagaManager.completeSaga(sagaIdentifier);

        assertEquals(false, retVal);
        Optional<Saga> sagaOpt = sagaRepository.findById("failed-notcompleted");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.FALSE, saga.isCompleted());
    }

    @Test
    public void testHasSagaFailed_whenSagaFailedNotCompleted_thenReturnTrue() {
        assertEquals(true, sagaManager.hasSagaFailed(new SagaIdentifier("failed-notcompleted")));
    }

    @Test
    public void testHasSagaCompleted_whenSagaFailedNotCompleted_thenReturnFalse() {
        assertEquals(false, sagaManager.hasSagaCompleted(new SagaIdentifier("failed-notcompleted")));
    }

}
