package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Resolve Request Resource
 * @summary
 * Resource for the actions over a request: approve, reject, return for review and cancel.
 * The comment is the rejection reason or the review comment, and is required in those cases.
 *
 * actorId is sent in the body until IAM is implemented; then it will come from the token.
 *
 * @since 1.0.0
 */
@Schema(name = "ResolveRequestRequest", description = "Request payload for approving, rejecting, returning or cancelling a request")
public record ResolveRequestResource(
        @NotNull(message = "{validation.not-null}") @Positive
        @Schema(description = "Employee that performs the action", example = "2")
        Long actorId,

        @Size(max = 500)
        @Schema(description = "Rejection reason or review comment", example = "Adjunta el descanso médico firmado", maxLength = 500)
        String comment
) {
}
