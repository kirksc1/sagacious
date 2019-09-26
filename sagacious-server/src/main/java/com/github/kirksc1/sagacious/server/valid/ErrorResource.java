package com.github.kirksc1.sagacious.server.valid;

/**
 * ErrorResource is a simple DTO used as a data structure for returning errors to service clients.
 */
public class ErrorResource {
    private String code;
    private String scope;

    /**
     * Get the error code.
     * @return The error code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the error code.
     * @param code The error code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get a reference to the data that caused the error.
     * @return A reference to the data that caused the error.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set a reference to the data that caused the error.
     * @param scope A reference to the data that caused the error.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
}
