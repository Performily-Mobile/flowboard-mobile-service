package com.performily.flowboard.payroll.application.internal.queryservices;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.events.PayslipDownloadedEvent;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipByIdAndEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipDownloadUrlQuery;
import com.performily.flowboard.payroll.domain.model.queries.GetPayslipsByEmployeeIdQuery;
import com.performily.flowboard.payroll.domain.model.queries.PayslipDownloadDto;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import com.performily.flowboard.payroll.domain.services.PayrollQueryService;
import com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories.JpaPayslipRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollQueryServiceImpl implements PayrollQueryService {

    private final JpaPayslipRepository payslipRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PayrollQueryServiceImpl(JpaPayslipRepository payslipRepository, ApplicationEventPublisher eventPublisher) {
        this.payslipRepository = payslipRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Payslip> handle(GetPayslipsByEmployeeIdQuery query) {
        return payslipRepository.findAllByEmployeeIdAndPublicationStatusAndYear(
                query.employeeId(),
                PublicationStatus.PUBLISHED,
                query.year()
        );
    }

    @Override
    public Optional<Payslip> handle(GetPayslipByIdAndEmployeeIdQuery query) {
        return payslipRepository.findByIdAndEmployeeId(query.payslipId(), query.employeeId());
    }

    @Override
    public PayslipDownloadDto handle(GetPayslipDownloadUrlQuery query) {
        // 1. Verificar la titularidad de la boleta
        Payslip payslip = payslipRepository.findByIdAndEmployeeId(query.payslipId(), query.requesterId())
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada o acceso denegado"));

        if (!payslip.isVisibleTo(query.requesterId())) {
            throw new RuntimeException("La boleta aún no está publicada");
        }

        // 2. Generar el enlace temporal de descarga (Simulación del ObjectStoragePayslipFileService)
        String temporaryUrl = payslip.getFileUrl() + "?signature=tmp-" + System.currentTimeMillis();

        // 3. Emitir evento de auditoría para la Ley de Protección de Datos
        eventPublisher.publishEvent(new PayslipDownloadedEvent(payslip.getId(), payslip.getEmployeeId(), LocalDateTime.now()));

        // 4. Retornar los datos encapsulados
        return new PayslipDownloadDto(temporaryUrl, payslip.getFileName(), payslip.getContentType());
    }
}