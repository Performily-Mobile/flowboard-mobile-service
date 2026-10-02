package com.performily.flowboard.request.domain.model.aggregates;

import com.performily.flowboard.request.domain.model.entities.RequestHistory;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.events.*;
import com.performily.flowboard.request.domain.model.valueobjects.Approver;
import com.performily.flowboard.request.domain.model.valueobjects.FieldValue;
import com.performily.flowboard.request.domain.model.valueobjects.RequestPeriod;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Request Aggregate Root
 * @summary
 * Formal request of an employee (vacations, leave, permission, certificate...).
 * It keeps its type, period, form values, attachments, approver, status and history,
 * and controls the valid transitions of the flow.
 *
 * Invariants checked by the aggregate:
 * - Only active request types receive requests.
 * - All required fields of the type have a value and the attachment exists when the type requires it.
 * - A type that deducts balance needs a period of whole days.
 * - Only the assigned approver can approve, reject or return the request.
 * - Only the requester can resubmit or cancel it.
 * - Rejection reason and review comment are required.
 * - Transitions: IN_PROGRESS -> APPROVED | REJECTED | UNDER_REVIEW | CANCELLED;
 *   UNDER_REVIEW -> IN_PROGRESS (resubmit) | CANCELLED. APPROVED, REJECTED and CANCELLED are final.
 * - Every transition adds a RequestHistory entry with author and date.
 *
 * Rules that need other bounded contexts or other requests (the requester is ACTIVE,
 * the approver is resolved from Workspace, there is no overlapping request) are checked
 * by the application service.
 *
 * @since 1.0.0
 */
public class Request extends AbstractDomainAggregateRoot<Request> {
    private Long id;
    private EmployeeId requesterId;
    private RequestType requestType;
    private RequestPeriod period;
    private List<FieldValue> fieldValues;
    private List<FileReference> attachments;
    private Approver approver;
    private RequestStatus status;
    private LocalDateTime submittedAt;
    private final List<RequestHistory> history;

    /**
     * Submits a new request. It starts IN_PROGRESS and the first history entry is added.
     *
     * @param requesterId the requester
     * @param requestType the request type
     * @param period      the period, or null when the type does not need it
     * @param fieldValues the values of the form
     * @param attachments the attachments
     * @param approver    the approver resolved from Workspace
     */
    public Request(EmployeeId requesterId, RequestType requestType, RequestPeriod period,
                   List<FieldValue> fieldValues, List<FileReference> attachments, Approver approver) {
        this.requesterId = Objects.requireNonNull(requesterId, "Requester id cannot be null");
        this.requestType = Objects.requireNonNull(requestType, "Request type cannot be null");
        this.approver = Objects.requireNonNull(approver, "Approver cannot be null");
        if (!requestType.isActive()) {
            throw new IllegalArgumentException("Request type '%s' is not active".formatted(requestType.getName()));
        }
        if (approver.employeeId() != null && approver.employeeId().equals(requesterId)) {
            throw new IllegalArgumentException("The requester cannot be the approver of its own request");
        }
        validatePeriod(requestType, period);
        requestType.validate(fieldValues, attachments);
        this.period = period;
        this.fieldValues = copy(fieldValues);
        this.attachments = copy(attachments);
        this.status = RequestStatus.IN_PROGRESS;
        this.submittedAt = LocalDateTime.now();
        this.history = new ArrayList<>();
        this.history.add(new RequestHistory(null, RequestStatus.IN_PROGRESS, requesterId, null));
    }

    /**
     * Rebuilds an existing request. Used by the persistence assemblers.
     */
    public Request(Long id, EmployeeId requesterId, RequestType requestType, RequestPeriod period,
                   List<FieldValue> fieldValues, List<FileReference> attachments, Approver approver,
                   RequestStatus status, LocalDateTime submittedAt, List<RequestHistory> history) {
        this.id = id;
        this.requesterId = requesterId;
        this.requestType = requestType;
        this.period = period;
        this.fieldValues = copy(fieldValues);
        this.attachments = copy(attachments);
        this.approver = approver;
        this.status = status;
        this.submittedAt = submittedAt;
        this.history = new ArrayList<>(history == null ? List.of() : history);
    }

    /**
     * Approves the request.
     *
     * @param actorId the approver
     */
    public void approve(EmployeeId actorId) {
        requireStatus(RequestStatus.IN_PROGRESS, "approved");
        requireApprover(actorId);
        changeStatus(RequestStatus.APPROVED, actorId, null);
        registerDomainEvent(new RequestApprovedEvent(id, requesterId.value(), actorId.value(), requestedDays(),
                requestType.getBalanceDeduction().name(), LocalDateTime.now()));
    }

    /**
     * Rejects the request. The reason is required and stays visible for the requester.
     *
     * @param actorId the approver
     * @param reason  the rejection reason
     */
    public void reject(EmployeeId actorId, String reason) {
        requireStatus(RequestStatus.IN_PROGRESS, "rejected");
        requireApprover(actorId);
        requireText(reason, "Rejection reason is required");
        changeStatus(RequestStatus.REJECTED, actorId, reason);
        registerDomainEvent(new RequestRejectedEvent(id, requesterId.value(), actorId.value(), reason.trim(), LocalDateTime.now()));
    }

    /**
     * Returns the request to the requester asking for more information.
     *
     * @param actorId the approver
     * @param comment what the approver needs, required
     */
    public void returnForReview(EmployeeId actorId, String comment) {
        requireStatus(RequestStatus.IN_PROGRESS, "returned for review");
        requireApprover(actorId);
        requireText(comment, "Review comment is required");
        changeStatus(RequestStatus.UNDER_REVIEW, actorId, comment);
        registerDomainEvent(new RequestReturnedForReviewEvent(id, requesterId.value(), actorId.value(), comment.trim(), LocalDateTime.now()));
    }

    /**
     * Sends the request again after a review. The new values and attachments
     * replace the previous ones and are validated again against the type.
     *
     * @param actorId     the requester
     * @param fieldValues the new values of the form
     * @param attachments the new attachments
     * @param comment     an optional comment for the approver
     */
    public void resubmit(EmployeeId actorId, List<FieldValue> fieldValues, List<FileReference> attachments, String comment) {
        requireStatus(RequestStatus.UNDER_REVIEW, "resubmitted");
        requireRequester(actorId);
        requestType.validate(fieldValues, attachments);
        this.fieldValues = copy(fieldValues);
        this.attachments = copy(attachments);
        changeStatus(RequestStatus.IN_PROGRESS, actorId, comment);
    }

    /**
     * Cancels the request. Only the requester can do it and only while it is not resolved.
     *
     * @param actorId the requester
     */
    public void cancel(EmployeeId actorId) {
        if (isResolved()) {
            throw new IllegalStateException("Request is already %s and cannot be cancelled".formatted(status));
        }
        requireRequester(actorId);
        changeStatus(RequestStatus.CANCELLED, actorId, null);
        registerDomainEvent(new RequestCancelledEvent(id, requesterId.value(), actorId.value(), LocalDateTime.now()));
    }

    /**
     * Cancels the request on behalf of the system, for example when the requester is terminated.
     *
     * @param reason the reason saved in the history
     */
    public void cancelBySystem(String reason) {
        if (isResolved()) {
            throw new IllegalStateException("Request is already %s and cannot be cancelled".formatted(status));
        }
        changeStatus(RequestStatus.CANCELLED, null, reason);
        registerDomainEvent(new RequestCancelledEvent(id, requesterId.value(), null, LocalDateTime.now()));
    }

    /**
     * Changes the approver of a request that is not resolved yet, for example when
     * the direct manager of the requester changes.
     *
     * @param newApprover the new approver
     */
    public void reassignApprover(Approver newApprover) {
        Objects.requireNonNull(newApprover, "Approver cannot be null");
        if (isResolved()) {
            throw new IllegalStateException("A resolved request cannot change its approver");
        }
        if (newApprover.employeeId() != null && newApprover.employeeId().equals(requesterId)) {
            throw new IllegalArgumentException("The requester cannot be the approver of its own request");
        }
        this.approver = newApprover;
    }

    /**
     * Signals that this request has just been submitted and persisted.
     *
     * <p>Called by the repository adapter after the JPA identity has been assigned.</p>
     */
    public void onSubmitted() {
        registerDomainEvent(new RequestSubmittedEvent(id, requesterId.value(), approver.type().name(),
                approver.employeeId() == null ? null : approver.employeeId().value(),
                requestType.getName(), submittedAt));
    }

    /**
     * Checks whether the given employee is the assigned approver.
     *
     * @param employeeId the employee
     * @return true if it is the approver
     */
    public boolean isAssignedTo(EmployeeId employeeId) {
        return approver.isAssignedTo(employeeId);
    }

    /**
     * Number of requested calendar days, 0 when there is no period or it is of hours.
     *
     * @return the requested days
     */
    public int requestedDays() {
        return period == null ? 0 : period.days();
    }

    /**
     * Checks whether the request has a final status.
     *
     * @return true for APPROVED, REJECTED and CANCELLED
     */
    public boolean isResolved() {
        return status.isFinal();
    }

    private void changeStatus(RequestStatus newStatus, EmployeeId actorId, String comment) {
        history.add(new RequestHistory(status, newStatus, actorId, comment));
        this.status = newStatus;
    }

    private void requireStatus(RequestStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException("Only %s requests can be %s; this request is %s".formatted(expected, action, status));
        }
    }

    private void requireApprover(EmployeeId actorId) {
        if (actorId == null || !approver.isAssignedTo(actorId)) {
            throw new IllegalStateException("Only the assigned approver can resolve this request");
        }
    }

    private void requireRequester(EmployeeId actorId) {
        if (actorId == null || !requesterId.equals(actorId)) {
            throw new IllegalStateException("Only the requester can perform this action");
        }
    }

    private static void requireText(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void validatePeriod(RequestType requestType, RequestPeriod period) {
        if (requestType.deductsBalance() && (period == null || period.hasHours())) {
            throw new IllegalArgumentException("Request type '%s' deducts balance and requires a period of whole days"
                    .formatted(requestType.getName()));
        }
    }

    private static <T> List<T> copy(List<T> list) {
        return new ArrayList<>(list == null ? List.of() : list);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeeId getRequesterId() {
        return requesterId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public RequestPeriod getPeriod() {
        return period;
    }

    public List<FieldValue> getFieldValues() {
        return List.copyOf(fieldValues);
    }

    public List<FileReference> getAttachments() {
        return List.copyOf(attachments);
    }

    public Approver getApprover() {
        return approver;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public List<RequestHistory> getHistory() {
        return List.copyOf(history);
    }
}
