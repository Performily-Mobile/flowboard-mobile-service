package com.performily.flowboard.request.domain.model.events;

import java.time.LocalDateTime;

/**
 * Request Submitted Event
 * @summary
 * Domain event registered when a request is submitted. It is used to notify the approver.
 *
 * @param requestId          the request id
 * @param requesterId        the requester id
 * @param approverType       DIRECT_MANAGER or HR_STAFF
 * @param approverEmployeeId the approver id, null for HR_STAFF
 * @param requestTypeName    the name of the request type
 * @param occurredOn         the date and time of the event
 * @since 1.0.0
 */
public record RequestSubmittedEvent(Long requestId, Long requesterId, String approverType, Long approverEmployeeId, String requestTypeName, LocalDateTime occurredOn) {
}
