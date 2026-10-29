package com.performily.flowboard.request.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.request.domain.model.valueobjects.ApproverType;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestPersistenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Request Persistence Repository
 * @summary
 * Spring Data repository for request persistence entities.
 *
 * The queries that return requests bring the request type in the same query
 * (join fetch). The collections of each request (form values, attachments and
 * history) are loaded in batches thanks to the BatchSize of the entity.
 *
 * @since 1.0.0
 */
@Repository
public interface RequestPersistenceRepository extends JpaRepository<RequestPersistenceEntity, Long> {
    /**
     * Finds a request by id together with its request type.
     *
     * @param id the request id
     * @return the request, if found
     */
    @Query("""
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.id = :id
            """)
    Optional<RequestPersistenceEntity> findWithRequestTypeById(@Param("id") Long id);

    /**
     * Finds one page of the requests that match the optional filters, newest first.
     *
     * @param requesterId   the requester id, or null for all
     * @param status        the status, or null for all
     * @param requestTypeId the request type id, or null for all
     * @param pageable      the page to read
     * @return the page of requests
     */
    @Query(value = """
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where (:requesterId is null or r.requesterId = :requesterId)
              and (:status is null or r.status = :status)
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            order by r.submittedAt desc, r.id desc
            """,
            countQuery = """
            select count(r) from RequestPersistenceEntity r
            where (:requesterId is null or r.requesterId = :requesterId)
              and (:status is null or r.status = :status)
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            """)
    Page<RequestPersistenceEntity> findPageByFilters(@Param("requesterId") Long requesterId,
                                                     @Param("status") RequestStatus status,
                                                     @Param("requestTypeId") Long requestTypeId,
                                                     Pageable pageable);

    /**
     * Finds one page of the requests of an employee, newest first. The status filter is optional.
     *
     * @param requesterId the requester id
     * @param status      the status, or null for all
     * @param pageable    the page to read
     * @return the page of requests
     */
    @Query(value = """
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.requesterId = :requesterId
              and (:status is null or r.status = :status)
            order by r.submittedAt desc, r.id desc
            """,
            countQuery = """
            select count(r) from RequestPersistenceEntity r
            where r.requesterId = :requesterId
              and (:status is null or r.status = :status)
            """)
    Page<RequestPersistenceEntity> findPageByRequesterIdAndOptionalStatus(@Param("requesterId") Long requesterId,
                                                                          @Param("status") RequestStatus status,
                                                                          Pageable pageable);

    /**
     * Finds the requests of an employee with one of the given statuses.
     *
     * @param requesterId the requester id
     * @param statuses    the statuses
     * @return the list of requests
     */
    @Query("""
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.requesterId = :requesterId
              and r.status in :statuses
            """)
    List<RequestPersistenceEntity> findAllByRequesterIdAndStatusIn(@Param("requesterId") Long requesterId,
                                                                   @Param("statuses") Collection<RequestStatus> statuses);

    /**
     * Finds one page of the requests with the given approver and status, oldest first.
     * The approver employee id and the request type are optional filters.
     *
     * @param approverType       the approver type
     * @param approverEmployeeId the approver employee id, or null
     * @param status             the status
     * @param requestTypeId      the request type id, or null
     * @param pageable           the page to read
     * @return the page of requests
     */
    @Query(value = """
            select r from RequestPersistenceEntity r
            join fetch r.requestType
            where r.approverType = :approverType
              and (:approverEmployeeId is null or r.approverEmployeeId = :approverEmployeeId)
              and r.status = :status
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            order by r.submittedAt asc, r.id asc
            """,
            countQuery = """
            select count(r) from RequestPersistenceEntity r
            where r.approverType = :approverType
              and (:approverEmployeeId is null or r.approverEmployeeId = :approverEmployeeId)
              and r.status = :status
              and (:requestTypeId is null or r.requestType.id = :requestTypeId)
            """)
    Page<RequestPersistenceEntity> findPageByApprover(@Param("approverType") ApproverType approverType,
                                                      @Param("approverEmployeeId") Long approverEmployeeId,
                                                      @Param("status") RequestStatus status,
                                                      @Param("requestTypeId") Long requestTypeId,
                                                      Pageable pageable);

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
