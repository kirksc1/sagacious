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
public class SimpleSagaManagerNotCompletedNotFailedIntegrationTest {

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

        assertEquals(1, executor.calledCount);
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

        assertEquals(2, executor.calledCount);
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

        assertEquals(3, executor.calledCount);
    }

    @Test
    public void testCompleteSaga_whenSagaCompleted_thenSagaCompletedSetToTrue() {
        SagaIdentifier sagaIdentifier = new SagaIdentifier("one-participant");
        sagaManager.completeSaga(sagaIdentifier);

        assertEquals(true, sagaManager.hasSagaCompleted(sagaIdentifier));
        Optional<Saga> sagaOpt = sagaRepository.findById("one-participant");
        Saga saga = sagaOpt.get();

        assertEquals(Boolean.TRUE, saga.isCompleted());
        assertEquals(Boolean.FALSE, saga.isFailed());
    }

    @Test
    public void testHasSagaFailed_whenSagaNew_thenReturnFalse() {
        assertEquals(false, sagaManager.hasSagaFailed(new SagaIdentifier("zero-participants")));
    }

    @Test
    public void testHasSagaCompleted_whenSagaNew_thenReturnTrue() {
        assertEquals(false, sagaManager.hasSagaCompleted(new SagaIdentifier("zero-participants")));
    }
}
