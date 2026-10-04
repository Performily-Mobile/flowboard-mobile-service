package com.performily.flowboard.payroll.domain.model.queries;

public record GetPayslipsByEmployeeIdQuery(Long employeeId, Integer year) {
    public GetPayslipsByEmployeeIdQuery {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }
    }
}