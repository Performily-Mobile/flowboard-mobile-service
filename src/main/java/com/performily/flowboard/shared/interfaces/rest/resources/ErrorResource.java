package com.performily.flowboard.shared.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

/**
 * Standard error response resource returned from REST endpoints.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResource(
        String code,
        String message,
        @Nullable String details) {

    /**
     * Creates an ErrorResource from code and message.
     */
    public ErrorResource(String code, String message) {
        this(code, message, null);
    }
}

