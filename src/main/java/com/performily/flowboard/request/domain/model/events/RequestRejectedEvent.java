package com.performily.flowboard.request.domain.model.events;

import java.time.LocalDateTime;

/**
 * Request Rejected Event
 * @summary
 * Domain event registered when a request is rejected.
 *
 * @param requestId   the request id
 * @param requesterId the requester id
 * @param actorId     the approver that rejected it
 * @param reason      the rejection reason
 * @param occurredOn  the date and time of the event
 * @since 1.0.0
 */
public record RequestRejectedEvent(Long requestId, Long requesterId, Long actorId, String reason, LocalDateTime occurredOn) {
}
