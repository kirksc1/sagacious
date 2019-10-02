package com.github.kirksc1.sagacious.server;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static io.restassured.RestAssured.with;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "sagacious.server.endpoints.add-participant.enabled=false")
public class AddParticipantControllerDisabledIntegrationTest {

    @LocalServerPort
    int port;

    @Before
    public void before() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void testAddParticipant_whenAddParticipantEndpointDisabled_thenReturn404() throws IOException {
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
            .statusCode(404)
        ;
    }

}
