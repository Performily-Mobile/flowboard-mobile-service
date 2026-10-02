package com.performily.flowboard.request.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Request History Resource
 * @summary
 * Resource for one entry of the history of a request.
 *
 * @since 1.0.0
 */
@Schema(name = "RequestHistoryResponse", description = "Request history entry")
public record RequestHistoryResource(
        @Schema(description = "History entry identifier", example = "1") Long id,
        @Schema(description = "Previous status, null for the submission", example = "IN_PROGRESS") String previousStatus,
        @Schema(description = "New status", example = "APPROVED") String newStatus,
        @Schema(description = "Employee that made the change, null when it was the system", example = "2") Long actorId,
        @Schema(description = "Comment or reason", example = "Aprobado") String comment,
        @Schema(description = "Date and time of the change") LocalDateTime occurredAt
) {
}
