package com.github.kirksc1.sagacious.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.repository.Saga;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public class SynchronousReverseParticipantOrderStrategyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CompensatingActionManager manager = new CompensatingActionManager(new SimpleCompensatingActionDefinitionMatcher(), new ArrayList<>());
    private CrudRepository<Saga,String> repository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        repository = Mockito.mock(CrudRepository.class);
    }

    @Test
    public void testConstructor_whenNullRepository_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("repository");

        new SynchronousReverseParticipantOrderStrategy(null, manager, objectMapper);
    }

    @Test
    public void testConstructor_whenNullManager_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("CompensatingActionManager");

        new SynchronousReverseParticipantOrderStrategy(repository, null, objectMapper);
    }

    @Test
    public void testConstructor_whenNullObjectMapper_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ObjectMapper");

        new SynchronousReverseParticipantOrderStrategy(repository, manager, null);
    }
}
