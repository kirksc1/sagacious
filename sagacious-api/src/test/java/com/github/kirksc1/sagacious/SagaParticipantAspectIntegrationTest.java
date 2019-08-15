package com.github.kirksc1.sagacious;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    @Qualifier("earlyTestAspect")
    EarlyTestAspect earlyTestAspect;

    @Autowired
    @Qualifier("lateTestAspect")
    LateTestAspect lateTestAspect;

    @Autowired
    TestActionFactory testFactory;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {

        @Bean
        public TestActionFactory testFactory() {
            return new TestActionFactory();
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
        public TestIdentifierFactory participantIdentifierFactory() {
            return new TestIdentifierFactory();
        }

        @Bean
        public TestIdentifierFactory testIdentifierFactory() {
            return new TestIdentifierFactory();
        }

        @Bean
        public EarlyTestAspect earlyTestAspect(SagaRepository repository) {
            return new EarlyTestAspect(repository);
        }

        @Bean
        public LateTestAspect lateTestAspect(SagaRepository repository) {
            return new LateTestAspect(repository);
        }
    }

    @Getter
    static class TestActionFactory implements CompensatingActionDefinitionFactory<String> {
        private String item;

        @Override
        public CompensatingActionDefinition buildDefinition(String item) {
            this.item = item;

            return new CompensatingActionDefinition();
        }

        public void reset() {
            this.item = null;
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

        @SagaOrchestrated
        public void orderedParticipantIdentifierFactoryOrchestrate() {
            participant.orderedIdentifierFactoryParticipate();
        }

        @SagaOrchestrated
        public void participantDataOrchestrate() {
            participant.participantDataParticipate("1", "2");
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

        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String orderedIdentifierFactoryParticipate() {
            return null;
        }

        @SagaParticipant(actionDefinitionFactory = "testFactory")
        public String participantDataParticipate(String test, @ParticipantData String data) {
            return null;
        }
    }

    static class User {

    }

    @Aspect
    @Getter
    @RequiredArgsConstructor
    @Order(-1)
    public static class EarlyTestAspect {

        private final SagaRepository repository;
        private boolean participantAddedEarly = false;
        private boolean participantAddedLate = false;

        @Around("@annotation(com.github.kirksc1.sagacious.SagaParticipant)")
        public Object doWork(ProceedingJoinPoint joinPoint) throws Throwable {
            repository.findById(SagaContextHolder.getSagaContext().getIdentifier().toString())
                    .ifPresent(saga -> participantAddedEarly = !saga.getParticipants().isEmpty());

            Object retVal = joinPoint.proceed(joinPoint.getArgs());

            repository.findById(SagaContextHolder.getSagaContext().getIdentifier().toString())
                    .ifPresent(saga -> participantAddedLate = !saga.getParticipants().isEmpty());
            return retVal;
        }

        public void reset() {
            participantAddedEarly = false;
            participantAddedLate = false;
        }
    }

    @Aspect
    @Getter
    @RequiredArgsConstructor
    @Order(1)
    public static class LateTestAspect {

        private final SagaRepository repository;
        private boolean participantAddedEarly = false;
        private boolean participantAddedLate = false;

        @Around("@annotation(com.github.kirksc1.sagacious.SagaParticipant)")
        public Object doWork(ProceedingJoinPoint joinPoint) throws Throwable {
            repository.findById(SagaContextHolder.getSagaContext().getIdentifier().toString())
                    .ifPresent(saga -> participantAddedEarly = !saga.getParticipants().isEmpty());

            Object retVal = joinPoint.proceed(joinPoint.getArgs());

            repository.findById(SagaContextHolder.getSagaContext().getIdentifier().toString())
                    .ifPresent(saga -> participantAddedLate = !saga.getParticipants().isEmpty());
            return retVal;
        }

        public void reset() {
            participantAddedEarly = false;
            participantAddedLate = false;
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

        earlyTestAspect.reset();
        lateTestAspect.reset();

        testFactory.reset();
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

    @Test
    @Transactional
    public void testOrdered_whenSecondAspectOrderedLower_thenSecondAspectExecutedFirst() {
        orchestrator.orderedParticipantIdentifierFactoryOrchestrate();

        assertEquals(false, earlyTestAspect.isParticipantAddedEarly());
        assertEquals(true, earlyTestAspect.isParticipantAddedLate());
    }

    @Test
    @Transactional
    public void testOrdered_whenSecondAspectOrderedHigher_thenSecondAspectExecutedLast() {
        orchestrator.orderedParticipantIdentifierFactoryOrchestrate();

        assertEquals(false, lateTestAspect.isParticipantAddedEarly());
        assertEquals(false, lateTestAspect.isParticipantAddedLate());
    }

    @Test
    @Transactional
    public void testParticipantData_whenParticipantData_thenAddParticipantBasedOnParticipantData() {
        orchestrator.participantDataOrchestrate();

        assertEquals("2", testFactory.getItem());
    }

}
