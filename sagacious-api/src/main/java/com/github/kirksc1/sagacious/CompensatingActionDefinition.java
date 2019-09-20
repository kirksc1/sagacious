package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * The CompensatingActionDefinition describes the compensating action for a saga participant.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompensatingActionDefinition {

    private List<Attribute> attributes = new ArrayList<>();
    private List<Header> headers = new ArrayList<>();
    private String body;
    private String uri;

    /**
     * Retrieve the attributes for the action definition.
     * @return The attributes for the action definition.
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Set the attributes for the action definition.
     * @param attributes The attributes for the action definition.
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Retrieve the headers for the action definition.
     * @return The headers for the action definition.
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * Set the headers for the action definition.
     * @param headers The headers for the action definition.
     */
    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    /**
     * Retrieve the body for the action definition.
     * @return The body for the action definition.
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the body for the action definition.
     * @param body The body for the action definition.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Retrieve the URI for the action definition.
     * @return The URI for the action definition.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the URI for the action definition.
     * @param uri The URI for the action definition.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}
