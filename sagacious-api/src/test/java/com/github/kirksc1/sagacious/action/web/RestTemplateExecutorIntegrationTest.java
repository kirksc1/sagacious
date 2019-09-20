package com.github.kirksc1.sagacious.action.web;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.Header;
import com.github.kirksc1.sagacious.action.web.RestTemplateExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplateExecutorIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestController testController;

    private RestTemplateExecutor executor;

    @TestConfiguration
    static class TestConfig {

        @Bean
        TestController testController() {
            return new TestController();
        }

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

    }

    @RestController
    static class TestController {

        private boolean called = false;
        private String body = null;
        private String header = null;

        @RequestMapping(path = "/test", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
        public void post(@RequestBody String body, @RequestHeader("test-header") String header) {
            called = true;
            this.body = body;
            this.header = header;
        }

        public boolean isCalled() {
            return called;
        }

        public String getBody() {
            return body;
        }

        public String getHeader() {
            return header;
        }
    }

    @Before
    public void before() {
        executor = new RestTemplateExecutor(new RestTemplate());
    }

    @Test
    public void testActionSent() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("http://localhost:" + port + "/test");
        definition.setMethod("POST");
        definition.setBody("test-body");

        definition.getHeaders().add(new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        definition.getHeaders().add(new Header("test-header", "test-value"));

        boolean execSuccess = executor.execute(definition);

        assertEquals(true, execSuccess);
        assertEquals(true, testController.isCalled());
        assertEquals("test-value", testController.getHeader());
        assertEquals("test-body", testController.getBody());
    }

    @Test
    public void testSendException() {
        CompensatingActionDefinition definition = new CompensatingActionDefinition();
        definition.setUri("http://localhost/fail");
        definition.setMethod("POST");
        definition.setBody("test-body");

        definition.getHeaders().add(new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        boolean execSuccess = executor.execute(definition);

        assertEquals(false, execSuccess);
    }
}
