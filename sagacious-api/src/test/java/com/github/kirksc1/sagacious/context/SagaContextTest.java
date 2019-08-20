package com.github.kirksc1.sagacious.context;

import com.github.kirksc1.sagacious.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertSame;

public class SagaContextTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor_whenNullSagaManager_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("SagaManager");

        new SagaContext(null, new SagaIdentifier("test"));
    }

    @Test
    public void testConstructor_whenNullSagaIdentifier_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("identifier");

        new SagaContext(new TestSagaManager(), null);
    }

    @Test
    public void testConstructor_whenValuesProvided_thenValuesGettable() {
        TestSagaManager sagaManager = new TestSagaManager();
        SagaIdentifier sagaIdentifier = new SagaIdentifier("test");

        SagaContext context = new SagaContext(sagaManager, sagaIdentifier);

        assertSame(sagaManager, context.getSagaManager());
        assertSame(sagaIdentifier, context.getIdentifier());
    }

    static class TestSagaManager implements SagaManager {
        @Override
        public boolean createSaga(SagaIdentifier sagaIdentifier) {
            return false;
        }

        @Override
        public boolean addParticipant(SagaIdentifier sagaIdentifier, ParticipantIdentifier participantIdentifier, CompensatingActionDefinition compensatingAction) {
            return false;
        }

        @Override
        public boolean failSaga(SagaIdentifier sagaIdentifier) {
            return false;
        }

        @Override
        public boolean completeSaga(SagaIdentifier sagaIdentifier) {
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
    }
}
