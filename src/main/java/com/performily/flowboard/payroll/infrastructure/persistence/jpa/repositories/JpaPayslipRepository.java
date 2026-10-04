package com.performily.flowboard.payroll.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.payroll.domain.model.aggregates.Payslip;
import com.performily.flowboard.payroll.domain.model.valueobjects.PublicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaPayslipRepository extends JpaRepository<Payslip, Long> {
    
    // Se utiliza una consulta explícita para extraer el año del campo issueDate
    @Query("SELECT p FROM Payslip p WHERE p.employeeId = :employeeId AND p.publicationStatus = :status AND YEAR(p.issueDate) = :year")
    List<Payslip> findAllByEmployeeIdAndPublicationStatusAndYear(
            @Param("employeeId") Long employeeId, 
            @Param("status") PublicationStatus status, 
            @Param("year") Integer year
    );

    Optional<Payslip> findByIdAndEmployeeId(Long id, Long employeeId);
}