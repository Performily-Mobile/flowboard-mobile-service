package com.performily.flowboard.request.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.request.domain.model.valueobjects.ApproverType;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Request Persistence Entity
 * @summary
 * JPA persistence entity for requests.
 *
 * requester_id and approver_employee_id are logical references to Workspace,
 * so they are plain columns without foreign key. The values of the dynamic form
 * are stored in request_field_values with the primary key (request_id, field_key).
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
public class RequestPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "request_type_id", nullable = false)
    private RequestTypePersistenceEntity requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "approver_type", nullable = false, length = 20)
    private ApproverType approverType;

    @Column(name = "approver_employee_id")
    private Long approverEmployeeId;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @ElementCollection
    @CollectionTable(name = "request_field_values", joinColumns = @JoinColumn(name = "request_id"))
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", nullable = false, length = 500)
    private Map<String, String> fieldValues = new LinkedHashMap<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("uploadedAt ASC, id ASC")
    private List<RequestAttachmentPersistenceEntity> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("occurredAt ASC, id ASC")
    private List<RequestHistoryPersistenceEntity> history = new ArrayList<>();
}
