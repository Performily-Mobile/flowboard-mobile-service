package com.performily.flowboard.request.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.request.domain.model.valueobjects.ApproverType;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Request Persistence Repository
 * @summary
 * Spring Data repository for request persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface RequestPersistenceRepository extends JpaRepository<RequestPersistenceEntity, Long> {
    /**
     * Finds the requests of an employee, newest first. The status filter is optional.
     *
     * @param requesterId the requester id
     * @param status      the status, or null for all
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            where r.requesterId = :requesterId
              and (:status is null or r.status = :status)
            order by r.submittedAt desc, r.id desc
            """)
    List<RequestPersistenceEntity> findAllByRequesterIdAndOptionalStatus(@Param("requesterId") Long requesterId,
                                                                         @Param("status") RequestStatus status);

    /**
     * Finds the requests of an employee with one of the given statuses.
     *
     * @param requesterId the requester id
     * @param statuses    the statuses
     * @return the list of requests
     */
    List<RequestPersistenceEntity> findAllByRequesterIdAndStatusIn(Long requesterId, Collection<RequestStatus> statuses);

    /**
     * Finds the requests with the given approver and status, oldest first.
     * The approver employee id and the request type are optional filters.
     *
     * @param approverType       the approver type
     * @param approverEmployeeId the approver employee id, or null
     * @param status             the status
     * @param requestTypeId      the request type id, or null
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            where r.approverType = :approverType
              and (:approverEmployeeId is null or r.approverEmployeeId = :approverEmployeeId)
              and r.status = :status
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            order by r.submittedAt asc, r.id asc
            """)
    List<RequestPersistenceEntity> findAllByApprover(@Param("approverType") ApproverType approverType,
                                                     @Param("approverEmployeeId") Long approverEmployeeId,
                                                     @Param("status") RequestStatus status,
                                                     @Param("requestTypeId") Long requestTypeId);

    /**
     * Counts the requests of an employee with one of the given statuses whose
     * period shares at least one day with the given dates.
     *
     * @param requesterId the requester id
     * @param statuses    the statuses to consider
     * @param startDate   the start date
     * @param endDate     the end date
     * @return the number of overlapping requests
     */
    @Query("""
            select count(r) from RequestPersistenceEntity r
            where r.requesterId = :requesterId
              and r.status in :statuses
              and r.startDate is not null
              and r.startDate <= :endDate
              and r.endDate >= :startDate
            """)
    long countOverlapping(@Param("requesterId") Long requesterId,
                          @Param("statuses") Collection<RequestStatus> statuses,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    /**
     * Checks whether a request type already has requests.
     *
     * @param requestTypeId the request type id
     * @return true if it has
     */
    boolean existsByRequestTypeId(Long requestTypeId);
}
