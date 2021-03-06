package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.action.*;
import com.github.kirksc1.sagacious.annotation.Executable;
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

}
