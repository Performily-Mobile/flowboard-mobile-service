package com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.workspace.domain.model.valueobjects.AssignmentChangeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;

/**
 * Job Assignment Persistence Entity
 * @summary
 * JPA persistence entity for job assignments.
 *
 * It does not extend AuditableAbstractPersistenceEntity because the
 * job_assignments table only has created_at (the history is never updated,
 * only closed).
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "job_assignments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class JobAssignmentPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeePersistenceEntity employee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaPersistenceEntity area;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionPersistenceEntity position;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 20)
    private AssignmentChangeType changeType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}