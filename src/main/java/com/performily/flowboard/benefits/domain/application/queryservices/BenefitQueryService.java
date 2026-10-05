package com.performily.flowboard.benefits.application.queryservices;

import com.performily.flowboard.benefits.application.queryservices.views.*;
import com.performily.flowboard.benefits.domain.model.entities.BenefitType;
import com.performily.flowboard.benefits.domain.model.queries.*;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;

import java.util.List;
import java.util.Optional;

/**
 * Read operations on the catalog, the assignments, the balances and their movements.
 */
public interface BenefitQueryService {
    List<BenefitType> handle(GetAllBenefitTypesQuery query);

    Optional<BenefitType> handle(GetBenefitTypeByIdQuery query);

    List<BenefitAssignmentView> handle(GetBenefitAssignmentsQuery query);

    Result<EmployeeBenefitsView, ApplicationError> handle(GetBenefitAssignmentsByEmployeeIdQuery query);

    Result<AreaAssignmentPreview, ApplicationError> handle(GetAreaAssignmentPreviewQuery query);

    Result<VacationBalanceView, ApplicationError> handle(GetVacationBalanceByEmployeeIdQuery query);

    Result<List<VacationMovementView>, ApplicationError> handle(GetVacationMovementsByEmployeeIdQuery query);

    List<VacationBalanceView> handle(GetAllVacationBalancesQuery query);
}
