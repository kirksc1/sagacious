package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.strategy.SynchronousParticipantOrderStrategy;
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
public class SimpleSagaManagerIntegrationTest {

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
    public void testCreateSaga_whenIdentifierProvided_thenSagaCreatedWithNoParticipants() {
        sagaManager.createSaga(new SagaIdentifier("test"));

        Optional<Saga> sagaOpt = sagaRepository.findById("test");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals("test", saga.getIdentifier());
        assertEquals(Boolean.FALSE, saga.isFailed());
        assertEquals(Boolean.FALSE, saga.isCompleted());
        assertEquals(true, saga.getParticipants().isEmpty());
    }

    @Test
    public void testAddParticipant_whenDetailsProvided_thenParticipantAdded() {
        //data initialization from data.sql
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("zero-participants"), new ParticipantIdentifier("test"), definition);

        assertEquals(true, retVal);

        Optional<Saga> sagaOpt = sagaRepository.findById("zero-participants");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals(1, saga.getParticipants().size());

        Participant participant = saga.getParticipants().get(0);
        assertEquals("test", participant.getIdentifier());
        assertEquals(Boolean.FALSE, participant.getFailCompleted());
        assertEquals(1, participant.getOrderIndex().intValue());
        assertEquals("{\"attributes\":[],\"headers\":[]}", participant.getActionDefinition());
    }

    @Test
    public void testAddParticipant_whenDetailsProvidedAndSagaCompleted_thenParticipantAdded() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("completed"), new ParticipantIdentifier("test"), definition);

        assertEquals(true, retVal);

        Optional<Saga> sagaOpt = sagaRepository.findById("completed");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals(2, saga.getParticipants().size());

        Participant participant = saga.getParticipants().get(1);
        assertEquals("test", participant.getIdentifier());
        assertEquals(false, participant.getFailCompleted());
    }

    @Test
    public void testAddParticipant_whenDetailsProvidedAndSagaCompleted_thenActionNotExeuted() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("completed"), new ParticipantIdentifier("test"), definition);

        assertEquals(0, executor.getCalledCount());
    }

    @Test
    public void testAddParticipant_whenDetailsProvidedAndSagaFailed_thenParticipantAdded() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("failed"), new ParticipantIdentifier("test"), definition);

        assertEquals(false, retVal);

        Optional<Saga> sagaOpt = sagaRepository.findById("failed");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals(2, saga.getParticipants().size());

        Participant participant = saga.getParticipants().get(1);
        assertEquals("test", participant.getIdentifier());
        assertEquals(true, participant.getFailCompleted());
    }

    @Test
    public void testAddParticipant_whenDetailsProvidedAndSagaFailed_thenActionExecuted() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        boolean retVal = sagaManager.addParticipant(new SagaIdentifier("failed"), new ParticipantIdentifier("test"), definition);

        assertEquals(1, executor.getCalledCount());
    }

    @Test
    public void testFailSaga_whenSagaHasOneParticipantAndFails_thenSagaAndParticipantMarkedForFailureCompleted() {
        //data initialization from data.sql
        sagaManager.failSaga(new SagaIdentifier("one-participant"));

        Optional<Saga> sagaOpt = sagaRepository.findById("one-participant");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals("one-participant", saga.getIdentifier());
        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.TRUE, saga.isCompleted());
        assertEquals(1, saga.getParticipants().size());

        Participant participant = saga.getParticipants().get(0);
        assertEquals("one-participant-1", participant.getIdentifier());
        assertEquals(Boolean.TRUE, participant.getFailCompleted());
        assertEquals(1, participant.getOrderIndex().intValue());
    }

    @Test
    public void testFailSaga_whenSagaHasTwoParticipantsAndFailsAndSecondParticipantFails_thenSagaAndParticipantMarkedForFailureNotCompleted() {
        //data initialization from data.sql
        sagaManager.failSaga(new SagaIdentifier("two-participants"));

        Optional<Saga> sagaOpt = sagaRepository.findById("two-participants");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals("two-participants", saga.getIdentifier());
        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.FALSE, saga.isCompleted());
        assertEquals(2, saga.getParticipants().size());

        Participant participant1 = saga.getParticipants().get(0);
        assertEquals("two-participants-1", participant1.getIdentifier());
        assertEquals(Boolean.TRUE, participant1.getFailCompleted());
        assertEquals(1, participant1.getOrderIndex().intValue());

        Participant participant2 = saga.getParticipants().get(1);
        assertEquals("two-participants-2", participant2.getIdentifier());
        assertEquals(Boolean.FALSE, participant2.getFailCompleted());
        assertEquals(2, participant2.getOrderIndex().intValue());
    }

    @Test
    public void testFailSaga_whenSagaFailsAndSecondParticipantIsSuccessfulOnSecondAttempt_thenSagaAndParticipantsMarkedForFailureCompleted() {
        //data initialization from data.sql
        sagaManager.failSaga(new SagaIdentifier("two-participants"));
        sagaManager.failSaga(new SagaIdentifier("two-participants"));

        Optional<Saga> sagaOpt = sagaRepository.findById("two-participants");

        assertTrue(sagaOpt.isPresent());

        Saga saga = sagaOpt.get();
        assertEquals("two-participants", saga.getIdentifier());
        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.TRUE, saga.isCompleted());

        Participant participant1 = saga.getParticipants().get(0);
        assertEquals("two-participants-1", participant1.getIdentifier());
        assertEquals(Boolean.TRUE, participant1.getFailCompleted());

        Participant participant2 = saga.getParticipants().get(1);
        assertEquals("two-participants-2", participant2.getIdentifier());
        assertEquals(Boolean.TRUE, participant2.getFailCompleted());
    }

    @Test
    public void testEndToEnd_whenSagaCreatedWithParticipantsAndIsFailedAndWithFailingActionThatIsRetried_thenSagaFailureIsCompleted() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("test");
        sagaManager.createSaga(sagaIdentifier);

        boolean add1 = sagaManager.addParticipant(sagaIdentifier, new ParticipantIdentifier("test-1"), new CompensatingActionDefinition());
        boolean add2 = sagaManager.addParticipant(sagaIdentifier, new ParticipantIdentifier("test-2"), new CompensatingActionDefinition());

        assertEquals(true, add1);
        assertEquals(true, add2);

        sagaManager.failSaga(sagaIdentifier);

        Optional<Saga> sagaOpt = sagaRepository.findById("test");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.FALSE, saga.isCompleted());

        assertEquals(Boolean.TRUE, saga.getParticipants().get(0).getFailCompleted());
        assertEquals(Boolean.FALSE, saga.getParticipants().get(1).getFailCompleted());

        sagaManager.failSaga(sagaIdentifier);

        sagaOpt = sagaRepository.findById("test");
        saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.TRUE, saga.isCompleted());

        assertEquals(Boolean.TRUE, saga.getParticipants().get(0).getFailCompleted());
        assertEquals(Boolean.TRUE, saga.getParticipants().get(1).getFailCompleted());
    }

    @Test
    public void testCompleteSaga_whenSagaCompleted_thenSagaCompletedSetToTrue() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("one-participant");
        sagaManager.completeSaga(sagaIdentifier);

        assertEquals(true, sagaManager.hasSagaCompleted(sagaIdentifier));
        Optional<Saga> sagaOpt = sagaRepository.findById("one-participant");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isCompleted());
    }

    @Test
    public void testCompleteSaga_whenFailedSagaIsCompleted_thenSagaStateNotChanged() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("failed");
        boolean retVal = sagaManager.completeSaga(sagaIdentifier);

        assertEquals(false, retVal);
        Optional<Saga> sagaOpt = sagaRepository.findById("failed");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isFailed());
        assertEquals(Boolean.FALSE, saga.isCompleted());
    }

    @Test
    public void testFailSaga_whenCompletedSagaIsFailed_thenSagaStateNotChanged() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("completed");
        boolean retVal = sagaManager.failSaga(sagaIdentifier);

        assertEquals(false, retVal);
        Optional<Saga> sagaOpt = sagaRepository.findById("completed");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.FALSE, saga.isFailed());
        assertEquals(Boolean.TRUE, saga.isCompleted());
    }

    @Test
    public void testHasSagaFailed_whenSagaFailed_thenReturnTrue() {
        assertEquals(true, sagaManager.hasSagaFailed(new SagaIdentifier("failed")));
    }

    @Test
    public void testHasSagaFailed_whenSagaNotFailed_thenReturnFalse() {
        assertEquals(false, sagaManager.hasSagaFailed(new SagaIdentifier("completed")));
    }

    @Test
    public void testHasSagaCompleted_whenSagaCompleted_thenReturnTrue() {
        assertEquals(true, sagaManager.hasSagaCompleted(new SagaIdentifier("completed")));
    }

    @Test
    public void testHasSagaCompleted_whenSagaNotCompleted_thenReturnFalse() {
        assertEquals(false, sagaManager.hasSagaCompleted(new SagaIdentifier("failed")));
    }
}
