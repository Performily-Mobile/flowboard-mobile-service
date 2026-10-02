package com.performily.flowboard.request.domain.model.valueobjects;

/**
 * Approver Type
 * @summary
 * Criteria used to route a request: to the direct manager of the requester or,
 * when the requester has no direct manager, to the HR staff.
 *
 * @since 1.0.0
 */
public enum ApproverType {
    DIRECT_MANAGER,
    HR_STAFF
}
