package com.performily.flowboard.request.domain.model.valueobjects;

import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;

import java.util.Objects;

/**
 * Approver Value Object
 * @summary
 * Person or role that must resolve a request. It is resolved when the request is
 * submitted: the direct manager of the requester (Workspace) or, when the requester
 * has no direct manager, the HR staff.
 * DIRECT_MANAGER requires employeeId; HR_STAFF has no employeeId.
 *
 * @param type       the approver type
 * @param employeeId the approver employee id, only for DIRECT_MANAGER
 * @since 1.0.0
 */
public record Approver(ApproverType type, EmployeeId employeeId) {
    /**
     * Compact constructor for Approver.
     *
     * @throws IllegalArgumentException if the type and the employee id do not match
     */
    public Approver {
        Objects.requireNonNull(type, "Approver type cannot be null");
        if (type == ApproverType.DIRECT_MANAGER && employeeId == null) {
            throw new IllegalArgumentException("A DIRECT_MANAGER approver requires an employee id");
        }
        if (type == ApproverType.HR_STAFF && employeeId != null) {
            throw new IllegalArgumentException("An HR_STAFF approver cannot have an employee id");
        }
    }

    /**
     * Creates a direct manager approver.
     *
     * @param managerId the direct manager id
     * @return the approver
     */
    public static Approver directManager(EmployeeId managerId) {
        return new Approver(ApproverType.DIRECT_MANAGER, managerId);
    }

    /**
     * Creates an HR staff approver.
     * @return the approver
     */
    public static Approver hrStaff() {
        return new Approver(ApproverType.HR_STAFF, null);
    }

    /**
     * Checks whether the given employee is this approver.
     * For HR_STAFF the role is checked by IAM, so any actor is accepted here.
     *
     * @param actorId the employee that tries to resolve the request
     * @return true if the actor can resolve as this approver
     */
    public boolean isAssignedTo(EmployeeId actorId) {
        if (type == ApproverType.HR_STAFF) {
            return actorId != null;
        }
        return employeeId.equals(actorId);
    }
}
