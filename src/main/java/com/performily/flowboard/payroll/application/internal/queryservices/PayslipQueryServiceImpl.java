package com.performily.flowboard.payroll.application.internal.queryservices;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.payroll.application.internal.outboundservices.storage.PayslipFileStorageService;
import com.performily.flowboard.payroll.application.queryservices.PayslipQueryService;
import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.events.PayslipDownloadedEvent;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdAndEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipDownloadUrlQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByPayrollPeriodIdQuery;
import com.performily.flowboard.payroll.domain.model.valueobjects.PayslipDownloadLink;
import com.performily.flowboard.payroll.domain.repositories.PayslipRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Payslip Query Service Impl
 * @summary
 * Application service that resolves payslip read queries.
 *
 * @since 1.0.0
 */
@Service
public class PayslipQueryServiceImpl implements PayslipQueryService {
    private static final Duration DOWNLOAD_LINK_VALIDITY = Duration.ofMinutes(5);

    private final PayslipRepository payslipRepository;
    private final PayslipFileStorageService payslipFileStorageService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param payslipRepository         the {@link PayslipRepository} instance
     * @param payslipFileStorageService the {@link PayslipFileStorageService} instance
     * @param eventPublisher            the {@link ApplicationEventPublisher} instance
     */
    public PayslipQueryServiceImpl(PayslipRepository payslipRepository,
                                   PayslipFileStorageService payslipFileStorageService,
                                   ApplicationEventPublisher eventPublisher) {
        this.payslipRepository = payslipRepository;
        this.payslipFileStorageService = payslipFileStorageService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Payslip> handle(GetPayslipsByEmployeeIdQuery query) {
        return payslipRepository.findAllPublishedByEmployeeIdAndYear(query.employeeId(), query.year());
    }

    @Override
    public Optional<Payslip> handle(GetPayslipByIdAndEmployeeIdQuery query) {
        return payslipRepository.findById(query.payslipId())
                .filter(payslip -> payslip.isVisibleTo(new EmployeeId(query.employeeId())));
    }

    @Override
    public Result<PayslipDownloadLink, ApplicationError> handle(GetPayslipDownloadUrlQuery query) {
        var payslip = handle(new GetPayslipByIdAndEmployeeIdQuery(query.payslipId(), query.requesterId()));
        if (payslip.isEmpty())
            return Result.failure(ApplicationError.notFound("Payslip", query.payslipId().toString()));
        var file = payslip.get().getFile();
        var url = payslipFileStorageService.generateTemporaryDownloadUrl(file, DOWNLOAD_LINK_VALIDITY);
        eventPublisher.publishEvent(new PayslipDownloadedEvent(
                query.payslipId(), query.requesterId(), LocalDateTime.now()));
        return Result.success(new PayslipDownloadLink(
                url, file.fileName(), file.contentType(), LocalDateTime.now().plus(DOWNLOAD_LINK_VALIDITY)));
    }

    @Override
    public Optional<Payslip> handle(GetPayslipByIdQuery query) {
        return payslipRepository.findById(query.payslipId());
    }

    @Override
    public List<Payslip> handle(GetPayslipsByPayrollPeriodIdQuery query) {
        return payslipRepository.findAllByPayrollPeriodId(query.payrollPeriodId());
    }
}
