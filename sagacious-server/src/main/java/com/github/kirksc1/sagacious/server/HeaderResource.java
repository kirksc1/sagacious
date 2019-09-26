package com.github.kirksc1.sagacious.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * HeaderResource is a simple DTO used as a data structure for returning Header data.
 */
public class HeaderResource {

    @NotNull
    private final String name;
    @NotNull
    private final String value;

    /**
     * Construct a new Header with the provided name-value pair.
     * @param name The name of the header.
     * @param value The value of the header.
     */
    @JsonCreator
    public HeaderResource(@JsonProperty("name") String name, @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Retrieve the name of the header.
     * @return The name of the header.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the value of the header.
     * @return The value of the header.
     */
    public String getValue() {
        return value;
    }
}
