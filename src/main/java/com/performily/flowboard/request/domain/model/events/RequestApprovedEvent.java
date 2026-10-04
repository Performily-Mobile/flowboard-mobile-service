package com.performily.flowboard.request.domain.model.events;

import java.time.LocalDateTime;

/**
 * Request Approved Event
 * @summary
 * Domain event registered when a request is approved. Benefits uses it to
 * deduct the balance when balanceDeduction is not NONE.
 *
 * @param requestId        the request id
 * @param requesterId      the requester id
 * @param actorId          the approver that approved it
 * @param requestedDays    the requested days
 * @param balanceDeduction the balance deduction of the request type
 * @param occurredOn       the date and time of the event
 * @since 1.0.0
 */
public record RequestApprovedEvent(Long requestId, Long requesterId, Long actorId, int requestedDays, String balanceDeduction, LocalDateTime occurredOn) {
}
