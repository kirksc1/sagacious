package com.github.kirksc1.sagacious.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.SagaManager;
import com.github.kirksc1.sagacious.action.CompensatingActionExecutor;
import com.github.kirksc1.sagacious.annotation.Executable;
import com.github.kirksc1.sagacious.repository.Saga;
import com.github.kirksc1.sagacious.repository.SagaRepository;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddParticipantControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    SagaRepository sagaRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockCompensatingActionExecutor executor;

    @Autowired
    SagaManager sagaManager;

    @TestConfiguration
    static class TestConfig {
        @Bean
        MockCompensatingActionExecutor mockCompensatingActionExecutor() {
            return new MockCompensatingActionExecutor();
        }
    }

    @Executable(scheme = "http")
    static class MockCompensatingActionExecutor implements CompensatingActionExecutor {
        CompensatingActionDefinition definition;
        @Override
        public boolean execute(CompensatingActionDefinition definition) {
            this.definition = definition;
            return true;
        }

        public void reset() {
            this.definition = null;
        }
    }

    @Before
    public void before() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @After
    public void after() {
        sagaRepository.findById("zero-participants-1")
                .ifPresent(saga -> {
                    saga.setParticipants(new ArrayList<>());
                    sagaRepository.save(saga);
                });
        sagaRepository.findById("zero-participants-2")
                .ifPresent(saga -> {
                    saga.setParticipants(new ArrayList<>());
                    sagaRepository.save(saga);
                });
    }

    @Test
    @Transactional
    public void testAddParticipant_whenMinimumDetailsProvidedForExistingSaga_thenParticipantIsAdded() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");

        with()
                .body(participant)
                .contentType("application/json")
        .when()
                .post("/sagas/zero-participants-1/participants")
        .then()
            .statusCode(201)
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants-1");
        assertEquals(1, saga.get().getParticipants().size());
        assertEquals("parttest", saga.get().getParticipants().get(0).getIdentifier());

        CompensatingActionDefinition actionDefinition = objectMapper.readValue(saga.get().getParticipants().get(0).getActionDefinition(), CompensatingActionDefinition.class);
        assertEquals("http://test.com/test", actionDefinition.getUri());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenAllDetailsProvidedForExistingSaga_thenParticipantIsAdded() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");
        action.getHeaders().add(new HeaderResource("hname", "hvalue"));
        action.getAttributes().add(new AttributeResource("aname", "avalue"));
        action.setBody("testbody");

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants-2/participants")
                .then()
                .statusCode(201)
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants-2");
        assertEquals(1, saga.get().getParticipants().size());
        assertEquals("parttest", saga.get().getParticipants().get(0).getIdentifier());

        CompensatingActionDefinition actionDefinition = objectMapper.readValue(saga.get().getParticipants().get(0).getActionDefinition(), CompensatingActionDefinition.class);
        assertEquals("http://test.com/test", actionDefinition.getUri());
        assertEquals("testbody", actionDefinition.getBody());

        assertEquals(1, actionDefinition.getAttributes().size());
        assertEquals("aname", actionDefinition.getAttributes().get(0).getName());
        assertEquals("avalue", actionDefinition.getAttributes().get(0).getValue());

        assertEquals(1, actionDefinition.getHeaders().size());
        assertEquals("hname", actionDefinition.getHeaders().get(0).getName());
        assertEquals("hvalue", actionDefinition.getHeaders().get(0).getValue());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenParticipantIdentifierNotProvided_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("identifier"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenActionNotProvided_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("action"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenActionUriNotProvided_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("action.uri"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenHeaderNameNullForExistingSaga_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");
        action.getHeaders().add(new HeaderResource(null, "hvalue"));

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("action.headers[0].name"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenHeaderValueNullForExistingSaga_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");
        action.getHeaders().add(new HeaderResource("hname", null));

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("action.headers[0].value"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenAttributeNameNullForExistingSaga_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");
        action.getAttributes().add(new AttributeResource(null, "avalue"));

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("action.attributes[0].name"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenAttributeValueNullForExistingSaga_thenReturnValidationError() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");
        action.getAttributes().add(new AttributeResource("aname", null));

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/zero-participants/participants")
                .then()
                .statusCode(400)
                .body("size()", is(1))
                .body("[0].code", is("field.required"))
                .body("[0].scope", is("action.attributes[0].value"))
        ;

        Optional<Saga> saga = sagaRepository.findById("zero-participants");
        assertEquals(0, saga.get().getParticipants().size());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenSagaIdNotFound_thenReturnResourceNotFound() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://test.com/test");

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/not-found/participants")
                .then()
                .statusCode(404)
        ;

        Optional<Saga> saga = sagaRepository.findById("not-found");
        assertEquals(false, saga.isPresent());
    }

    @Test
    @Transactional
    public void testAddParticipant_whenSagaFailedCompleted_thenReturnTBD() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://localhost:" + port + "/test");

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/failed-completed/participants")
                .then()
                .statusCode(201)
        ;

        Optional<Saga> saga = sagaRepository.findById("failed-completed");
        assertEquals(1, saga.get().getParticipants().size());
        assertEquals("parttest", saga.get().getParticipants().get(0).getIdentifier());
        assertEquals(true, saga.get().getParticipants().get(0).getFailCompleted());

        assertNotNull(executor.definition);
    }

    @Test
    @Transactional
    public void testAddParticipant_whenSagaFailedNotCompleted_thenReturnTBD() throws IOException {
        ParticipantResource participant = new ParticipantResource();
        participant.setIdentifier("parttest");

        CompensatingActionDefinitionResource action = new CompensatingActionDefinitionResource();
        participant.setAction(action);

        action.setUri("http://localhost:" + port + "/test");

        with()
                .body(participant)
                .contentType("application/json")
                .when()
                .post("/sagas/failed-notcompleted/participants")
                .then()
                .statusCode(201)
        ;

        Optional<Saga> saga = sagaRepository.findById("failed-notcompleted");
        assertEquals(1, saga.get().getParticipants().size());
        assertEquals("parttest", saga.get().getParticipants().get(0).getIdentifier());
        assertEquals(true, saga.get().getParticipants().get(0).getFailCompleted());

        assertNotNull(executor.definition);
    }

}
