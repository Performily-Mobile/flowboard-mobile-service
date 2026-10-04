package com.performily.flowboard.request.domain.model.events;

import java.time.LocalDateTime;

/**
 * Request Cancelled Event
 * @summary
 * Domain event registered when a request is cancelled, by the requester or by the
 * system when the requester is terminated.
 *
 * @param requestId   the request id
 * @param requesterId the requester id
 * @param actorId     who cancelled it, null when it was the system
 * @param occurredOn  the date and time of the event
 * @since 1.0.0
 */
public record RequestCancelledEvent(Long requestId, Long requesterId, Long actorId, LocalDateTime occurredOn) {
}
