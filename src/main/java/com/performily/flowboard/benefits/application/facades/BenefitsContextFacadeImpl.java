package com.performily.flowboard.benefits.application.facades;

import com.performily.flowboard.benefits.domain.model.aggregates.VacationBalance;
import com.performily.flowboard.benefits.domain.model.valueobjects.VacationDays;
import com.performily.flowboard.benefits.domain.repositories.VacationBalanceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of {@link BenefitsContextFacade} over the vacation balance repository.
 */
@Service
public class BenefitsContextFacadeImpl implements BenefitsContextFacade {
    private final VacationBalanceRepository vacationBalanceRepository;

    public BenefitsContextFacadeImpl(VacationBalanceRepository vacationBalanceRepository) {
        this.vacationBalanceRepository = vacationBalanceRepository;
    }

    @Override
    public boolean hasAvailableVacationDays(Long employeeId, BigDecimal days) {
        if (employeeId == null || days == null || days.signum() <= 0) {
            return false;
        }
        return vacationBalanceRepository.findByEmployeeId(employeeId)
                .map(balance -> balance.hasEnough(new VacationDays(days)))
                .orElse(false);
    }

    @Override
    public BigDecimal fetchAvailableDays(Long employeeId) {
        return vacationBalanceRepository.findByEmployeeId(employeeId)
                .map(VacationBalance::availableDays)
                .map(VacationDays::value)
                .orElse(VacationDays.ZERO.value());
    }
}
