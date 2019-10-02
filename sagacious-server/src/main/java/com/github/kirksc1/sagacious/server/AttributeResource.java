package com.github.kirksc1.sagacious.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * AttributeResource is a simple DTO used as a data structure for returning Attribute data.
 */
public class AttributeResource {

    @NotNull
    private final String name;
    @NotNull
    private final String value;

    /**
     * Construct a new Attribute with the provided name-value pair.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    @JsonCreator
    public AttributeResource(@JsonProperty("name") String name, @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Retrieve the name of the attribute.
     * @return The name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the value of the attribute.
     * @return The value of the attribute.
     */
    public String getValue() {
        return value;
    }

}
