package com.performily.flowboard.benefits.application.queryservices.views;

import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;

import java.util.List;

/**
 * A vacation balance with the name and area of its employee.
 *
 * @param movements newest first; empty in the list of balances
 */
public record VacationBalanceView(VacationBalance balance, String employeeName, String areaName,
                                  List<VacationMovementView> movements) {
}
