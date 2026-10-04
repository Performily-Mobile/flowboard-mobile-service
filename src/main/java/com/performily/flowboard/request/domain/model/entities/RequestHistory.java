package com.performily.flowboard.request.domain.model.entities;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Request History Entity
 * @summary
 * One entry of the history of a request: the status change, who made it, the
 * comment and the date. previousStatus is null for the first entry (submission).
 * actorId is null when the change is made by the system (for example when the
 * requester is terminated).
 *
 * @since 1.0.0
 */
public class RequestHistory {
    private static final int COMMENT_MAX_LENGTH = 500;

    private Long id;
    private final RequestStatus previousStatus;
    private final RequestStatus newStatus;
    private final EmployeeId actorId;
    private final String comment;
    private final LocalDateTime occurredAt;

    /**
     * Creates a new history entry with the current date and time.
     */
    public RequestHistory(RequestStatus previousStatus, RequestStatus newStatus, EmployeeId actorId, String comment) {
        this(null, previousStatus, newStatus, actorId, comment, LocalDateTime.now());
    }

    /**
     * Rebuilds an existing history entry. Used by the persistence assemblers.
     */
    public RequestHistory(Long id, RequestStatus previousStatus, RequestStatus newStatus, EmployeeId actorId,
                          String comment, LocalDateTime occurredAt) {
        this.id = id;
        this.previousStatus = previousStatus;
        this.newStatus = Objects.requireNonNull(newStatus, "New status cannot be null");
        this.actorId = actorId;
        if (comment != null && comment.trim().length() > COMMENT_MAX_LENGTH) {
            throw new IllegalArgumentException("Comment cannot exceed %d characters".formatted(COMMENT_MAX_LENGTH));
        }
        this.comment = comment == null || comment.isBlank() ? null : comment.trim();
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestStatus getPreviousStatus() {
        return previousStatus;
    }

    public RequestStatus getNewStatus() {
        return newStatus;
    }

    public EmployeeId getActorId() {
        return actorId;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
