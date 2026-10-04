package com.performily.flowboard.request.domain.model.events;

import java.time.LocalDateTime;

/**
 * Request Returned For Review Event
 * @summary
 * Domain event registered when the approver returns a request to the requester
 * asking for more information.
 *
 * @param requestId   the request id
 * @param requesterId the requester id
 * @param actorId     the approver that returned it
 * @param comment     the review comment
 * @param occurredOn  the date and time of the event
 * @since 1.0.0
 */
public record RequestReturnedForReviewEvent(Long requestId, Long requesterId, Long actorId, String comment, LocalDateTime occurredOn) {
}
