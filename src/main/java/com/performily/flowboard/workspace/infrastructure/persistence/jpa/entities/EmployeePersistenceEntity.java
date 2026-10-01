package com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.performily.flowboard.workspace.domain.model.valueobjects.BirthDate;
import com.performily.flowboard.workspace.domain.model.valueobjects.ContractType;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.domain.model.valueobjects.PhoneNumber;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.converters.BirthDatePersistenceConverter;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.converters.PhoneNumberPersistenceConverter;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Employee Persistence Entity
 * @summary
 * JPA persistence entity for employees.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class EmployeePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Embedded
    private PersonNamePersistenceEmbeddable name;

    @Embedded
    private IdentityDocumentPersistenceEmbeddable identityDocument;

    @Convert(converter = BirthDatePersistenceConverter.class)
    @Column(name = "birth_date", nullable = false)
    private BirthDate birthDate;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(name = "email", nullable = false, length = 120)
    private EmailAddress email;

    @Convert(converter = PhoneNumberPersistenceConverter.class)
    @Column(name = "phone_number", nullable = false, length = 16)
    private PhoneNumber phoneNumber;

    @Embedded
    private AddressPersistenceEmbeddable address;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 20)
    private ContractType contractType;

    @Embedded
    private EmploymentPeriodPersistenceEmbeddable employmentPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentStatus status;

    @Embedded
    private TerminationDetailsPersistenceEmbeddable termination;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaPersistenceEntity area;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionPersistenceEntity position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direct_manager_id")
    private EmployeePersistenceEntity directManager;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDate ASC, id ASC")
    private List<JobAssignmentPersistenceEntity> jobAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("uploadedAt ASC")
    private List<EmployeeDocumentPersistenceEntity> documents = new ArrayList<>();
}