package com.github.kirksc1.sagacious.action.web;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.action.CompensatingActionExecutor;
import com.github.kirksc1.sagacious.annotation.Executable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Executable(scheme="http")
@Executable(scheme="https")
public class RestTemplateExecutor implements CompensatingActionExecutor, Ordered {

    private static final int DEFAULT_ORDER = 0;

    @NonNull
    private final RestTemplate restTemplate;

    private final int order;

    public RestTemplateExecutor(RestTemplate restTemplate) {
        this(restTemplate, DEFAULT_ORDER);
    }

    @Override
    public boolean execute(CompensatingActionDefinition definition) {
        boolean retVal = false;

        URI uri = URI.create(definition.getUri());
        HttpMethod method = HttpMethod.resolve(definition.getMethod());

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

    @Override
    public int getOrder() {
        return order;
    }
}
