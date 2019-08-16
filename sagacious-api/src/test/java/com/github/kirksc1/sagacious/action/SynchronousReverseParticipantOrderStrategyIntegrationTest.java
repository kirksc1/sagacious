package com.github.kirksc1.sagacious.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.CompensatingActionDefinition;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SynchronousReverseParticipantOrderStrategyIntegrationTest {

    @Autowired
    CompensatingActionStrategy strategy;

    @Autowired
    JpaRepository<Saga, String> sagaRepository;

    @Autowired
    TestCompensatingActionExecutor executor;

    @Before
    public void before() {
        executor.reset();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CompensatingActionStrategy compensatingActionStrategy(CrudRepository<Saga, String> repository, CompensatingActionManager manager, ObjectMapper objectMapper) {
            return new SynchronousReverseParticipantOrderStrategy(repository, manager, objectMapper);
        }

        @Bean
        public TestCompensatingActionExecutor compensatingActionExecutor() {
            return new TestCompensatingActionExecutor();
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
    static class TestCompensatingActionExecutor implements CompensatingActionExecutor {

        private final List<String> uris = new ArrayList<>();

        @Override
        public boolean execute(CompensatingActionDefinition definition) {
            uris.add(definition.getUri());
            return true;
        }

        public List<String> getUris() {
            return uris;
        }

        public void reset() {
            uris.clear();
        }
    }

    @Test
    public void testPerformCompensatingActions_whenExecuted_thenParticipantsAreExecutedInReverseOrder() {
        Optional<Saga> sagaOpt = sagaRepository.findById("two-participants");
        strategy.performCompensatingActions(sagaOpt.get());

        assertEquals(2, executor.getUris().size());
        assertEquals("2", executor.getUris().get(0));
        assertEquals("1", executor.getUris().get(1));

        sagaOpt = sagaRepository.findById("two-participants");

        sagaOpt.get().getParticipants().stream()
                .forEach(participant -> assertEquals(true, participant.getFailCompleted()));
    }
}
