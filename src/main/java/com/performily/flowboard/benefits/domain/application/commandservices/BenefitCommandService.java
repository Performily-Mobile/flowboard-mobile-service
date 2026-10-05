package com.performily.flowboard.benefits.application.commandservices;

import com.performily.flowboard.benefits.domain.model.aggregates.BenefitAssignment;
import com.performily.flowboard.benefits.domain.model.commands.*;
import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

/**
 * Write operations on the benefit catalog, the assignments and the deliveries.
 */
public interface BenefitCommandService {
    Result<BenefitType, ApplicationError> handle(CreateBenefitTypeCommand command);

    Result<BenefitType, ApplicationError> handle(ActivateBenefitTypeCommand command);

    Result<BenefitType, ApplicationError> handle(DeactivateBenefitTypeCommand command);

    Result<BenefitAssignment, ApplicationError> handle(AssignBenefitCommand command);

    Result<AreaAssignmentResult, ApplicationError> handle(AssignBenefitToAreaCommand command);

    Result<BenefitAssignment, ApplicationError> handle(RegisterBenefitDeliveryCommand command);

    Result<BenefitAssignment, ApplicationError> handle(CancelBenefitAssignmentCommand command);

    /**
     * Cancels the benefits not delivered yet of an employee that left the company.
     *
     * @return the number of cancelled assignments
     */
    int handle(CancelPendingBenefitAssignmentsCommand command);
}
