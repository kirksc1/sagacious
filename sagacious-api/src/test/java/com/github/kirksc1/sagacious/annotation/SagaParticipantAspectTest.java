package com.github.kirksc1.sagacious.annotation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertEquals;

public class SagaParticipantAspectTest {

    private ApplicationContext context;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        context = Mockito.mock(ApplicationContext.class);
    }

    @Test
    public void testConstructorApplicationContext_whenContextNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ApplicationContext");

        new SagaParticipantAspect(null);
    }

    @Test
    public void testConstructorApplicationContextInt_whenContextNull_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ApplicationContext");

        new SagaParticipantAspect(null, 0);
    }

    @Test
    public void testConstructorApplicationContextInt_whenOrderProvided_thenOrderGettable() {
        SagaParticipantAspect aspect = new SagaParticipantAspect(context, 5);

        assertEquals(5, aspect.getOrder());
    }
}
