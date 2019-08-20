package com.github.kirksc1.sagacious;

import org.springframework.util.Assert;

/**
 * A name-value pair to be communicated in the compensating action.
 */
public class Header {

    private final String name;
    private final String value;

    /**
     * Construct a new Header with the provided name-value pair.
     * @param name The name of the header.
     * @param value The value of the header.
     */
    public Header(String name, String value) {
        Assert.notNull(name, "The name provided is null");
        Assert.notNull(value, "The value provided is null");

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
