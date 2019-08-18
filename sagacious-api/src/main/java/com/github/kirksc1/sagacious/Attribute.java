package com.github.kirksc1.sagacious;

import org.springframework.util.Assert;

/**
 * A general purpose name-value pair used to describe the compensating action.
 */
public class Attribute {

    private final String name;
    private final String value;

    /**
     * Construct a new Attribute with the provided name-value pair.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    public Attribute(String name, String value) {
        Assert.notNull(name, "The name provided is null");
        Assert.notNull(value, "The value provided is null");

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
