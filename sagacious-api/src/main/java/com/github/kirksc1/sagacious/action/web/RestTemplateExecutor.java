package com.github.kirksc1.sagacious.action.web;

import com.github.kirksc1.sagacious.Attribute;
import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.action.CompensatingActionExecutor;
import com.github.kirksc1.sagacious.annotation.Executable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

/**
 * RestTemplateExecutor is a concrete implementation of the {@link CompensatingActionExecutor} interface
 * that can execute HTTP(S) web requests as defined within a {@link CompensatingActionDefinition}.
 */
@Slf4j
@Executable(scheme="http")
@Executable(scheme="https")
public class RestTemplateExecutor implements CompensatingActionExecutor, Ordered {

    /**
     * Name of the attribute used to identify the desired HTTP method.
     */
    public static final String ATTRIBUTE_HTTP_METHOD = "http.method";

    private static final int DEFAULT_ORDER = 0;

    private final RestTemplate restTemplate;
    private final int order;

    /**
     * Construct a new RestTemplateExecutor with the provided RestTemplate.
     * @param restTemplate The RestTemplate.
     */
    public RestTemplateExecutor(RestTemplate restTemplate) {
        this(restTemplate, DEFAULT_ORDER);
    }

    /**
     * Construct a new RestTemplateExecutor with the provided RestTemplate and order.
     * @param restTemplate The RestTemplate.
     * @param order The spring Ordered value of the CompensatingActionExecutor.
     */
    public RestTemplateExecutor(RestTemplate restTemplate, int order) {
        Assert.notNull(restTemplate, "The RestTemplate provided is null");

        this.restTemplate = restTemplate;
        this.order = order;
    }

    /**
     * {@inheritDoc}
     * <p>The RestTemplateExecutor initiates web requests according to the {@link CompensatingActionDefinition}</p>
     */
    @Override
    public boolean execute(CompensatingActionDefinition definition) {
        boolean retVal = false;

        URI uri = URI.create(definition.getUri());

        HttpMethod method = Optional.ofNullable(definition.getBody())
                .map(s -> HttpMethod.POST)
                .orElse(HttpMethod.DELETE);

        method = findHttpMethod(definition).orElse(method);

        HttpHeaders httpHeaders = new HttpHeaders();
        Optional.ofNullable(definition.getHeaders())
                .ifPresent(headers -> headers.stream()
                        .forEach(header -> httpHeaders.add(header.getName(), header.getValue()))
                );

        HttpEntity<String> httpEntity = new HttpEntity<>(definition.getBody(), httpHeaders);

        try {
            restTemplate.exchange(uri, method, httpEntity, String.class);
            retVal = true;
        } catch (RuntimeException e) {
            //TODO add ability to log saga ID and participant ID
            log.error("An error occurred during compensating action=" + definition, e);
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Find an HttpMethod, if present, to override the default behavior.  It resolves the
     * HttpMethod from the attributes.
     * @param definition The CompensatingActionDefinition.
     * @return The HttpMethod if found.
     */
    protected Optional<HttpMethod> findHttpMethod(CompensatingActionDefinition definition) {
        HttpMethod retVal = null;

        for (Attribute attribute : definition.getAttributes()) {
            if (ATTRIBUTE_HTTP_METHOD.equals(attribute.getName())) {
                retVal = HttpMethod.resolve(attribute.getValue());
            }
        }
        return Optional.ofNullable(retVal);
    }
}
