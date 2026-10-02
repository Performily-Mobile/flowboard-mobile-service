package com.performily.flowboard.request.infrastructure.persistence.jpa.entities;

import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request History Persistence Entity
 * @summary
 * JPA persistence entity for the history of a request. The history is never
 * updated, only new entries are added.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "request_history")
@Getter
@Setter
@NoArgsConstructor
public class RequestHistoryPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private RequestPersistenceEntity request;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private RequestStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private RequestStatus newStatus;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(length = 500)
    private String comment;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}
