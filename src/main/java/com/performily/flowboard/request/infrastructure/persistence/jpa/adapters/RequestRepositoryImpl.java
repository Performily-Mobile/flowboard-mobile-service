package com.performily.flowboard.request.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.valueobjects.ApproverType;
import com.performily.flowboard.request.domain.model.valueobjects.RequestStatus;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.request.infrastructure.persistence.jpa.assemblers.RequestPersistenceAssembler;
import com.performily.flowboard.request.infrastructure.persistence.jpa.entities.RequestPersistenceEntity;
import com.performily.flowboard.request.infrastructure.persistence.jpa.repositories.RequestPersistenceRepository;
import com.performily.flowboard.request.infrastructure.persistence.jpa.repositories.RequestTypePersistenceRepository;
import com.performily.flowboard.shared.domain.model.valueobjects.PageCriteria;
import com.performily.flowboard.shared.domain.model.valueobjects.PagedResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Request Repository Impl
 * @summary
 * Repository adapter that bridges the request domain repository port with Spring Data JPA.
 *
 * Also acts as the event-publishing boundary: after the {@link Request} is persisted,
 * the domain events registered by the aggregate are dispatched via Spring's
 * {@link ApplicationEventPublisher}. For a new request, a RequestSubmittedEvent is
 * added once the JPA-assigned id is available.
 *
 * Methods run inside a transaction so the lazy collections (form values,
 * attachments, history and the fields of the type) can be read while assembling
 * the domain object. The read methods use read-only transactions, so Hibernate
 * does not keep a copy of each loaded entity to look for changes.
 *
 * A new request is converted to a new entity. An existing request is saved by
 * loading its stored entity and copying only what changed over it, so the
 * attachments and form values that did not change are not deleted and inserted again.
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class RequestRepositoryImpl implements RequestRepository {
    private static final List<RequestStatus> UNRESOLVED_STATUSES =
            List.of(RequestStatus.IN_PROGRESS, RequestStatus.UNDER_REVIEW);
    private static final List<RequestStatus> OCCUPYING_STATUSES =
            List.of(RequestStatus.IN_PROGRESS, RequestStatus.UNDER_REVIEW, RequestStatus.APPROVED);

    private final RequestPersistenceRepository requestPersistenceRepository;
    private final RequestTypePersistenceRepository requestTypePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param requestPersistenceRepository the {@link RequestPersistenceRepository} instance
     * @param requestTypePersistenceRepository the {@link RequestTypePersistenceRepository} instance
     * @param eventPublisher the {@link ApplicationEventPublisher} instance
     */
    public RequestRepositoryImpl(RequestPersistenceRepository requestPersistenceRepository,
                                 RequestTypePersistenceRepository requestTypePersistenceRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.requestPersistenceRepository = requestPersistenceRepository;
        this.requestTypePersistenceRepository = requestTypePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> findById(Long id) {
        return requestPersistenceRepository.findWithRequestTypeById(id).map(RequestPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllByRequesterId(Long requesterId, RequestStatus status) {
        return requestPersistenceRepository.findAllByRequesterIdAndOptionalStatus(requesterId, status).stream()
                .map(RequestPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Request> findAllByRequesterId(Long requesterId, RequestStatus status, PageCriteria page) {
        return toPagedResult(requestPersistenceRepository.findPageByRequesterIdAndOptionalStatus(
                requesterId, status, PageRequest.of(page.page(), page.size())), page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllUnresolvedByRequesterId(Long requesterId) {
        return requestPersistenceRepository.findAllByRequesterIdAndStatusIn(requesterId, UNRESOLVED_STATUSES).stream()
                .map(RequestPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllPendingByApproverEmployeeId(Long approverId, Long requestTypeId) {
        return requestPersistenceRepository.findAllByApprover(
                        ApproverType.DIRECT_MANAGER, approverId, RequestStatus.IN_PROGRESS, requestTypeId).stream()
                .map(RequestPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Request> findAllPendingByApproverEmployeeId(Long approverId, Long requestTypeId, PageCriteria page) {
        return toPagedResult(requestPersistenceRepository.findPageByApprover(ApproverType.DIRECT_MANAGER, approverId,
                RequestStatus.IN_PROGRESS, requestTypeId, PageRequest.of(page.page(), page.size())), page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findAllPendingForHrStaff(Long requestTypeId) {
        return requestPersistenceRepository.findAllByApprover(
                        ApproverType.HR_STAFF, null, RequestStatus.IN_PROGRESS, requestTypeId).stream()
                .map(RequestPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Request> findAllPendingForHrStaff(Long requestTypeId, PageCriteria page) {
        return toPagedResult(requestPersistenceRepository.findPageByApprover(ApproverType.HR_STAFF, null,
                RequestStatus.IN_PROGRESS, requestTypeId, PageRequest.of(page.page(), page.size())), page);
    }

    @Override
    public Request save(Request request) {
        boolean isNew = request.getId() == null;
        var pendingEvents = new ArrayList<>(request.domainEvents());
        request.clearDomainEvents();
        var entity = isNew
                ? RequestPersistenceAssembler.toPersistenceFromDomain(request,
                        requestTypePersistenceRepository.getReferenceById(request.getRequestType().getId()))
                : updateStoredEntity(request);
        var savedEntity = requestPersistenceRepository.saveAndFlush(entity);
        var savedRequest = RequestPersistenceAssembler.toDomainFromPersistence(savedEntity);
        if (isNew) {
            savedRequest.onSubmitted();
            pendingEvents.addAll(savedRequest.domainEvents());
            savedRequest.clearDomainEvents();
        }
        pendingEvents.forEach(eventPublisher::publishEvent);
        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOverlappingRequest(Long requesterId, LocalDate startDate, LocalDate endDate) {
        return requestPersistenceRepository.countOverlapping(requesterId, OCCUPYING_STATUSES, startDate, endDate) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRequestTypeId(Long requestTypeId) {
        return requestPersistenceRepository.existsByRequestTypeId(requestTypeId);
    }

    /**
     * Loads the stored entity of an existing request and copies the domain state over it.
     *
     * @param request the {@link Request} instance with id
     * @return the stored entity, already updated
     * @throws IllegalArgumentException if the request does not exist
     */
    /** Converts a page of entities into a page of domain requests. */
    private static PagedResult<Request> toPagedResult(Page<RequestPersistenceEntity> page, PageCriteria criteria) {
        var items = page.getContent().stream().map(RequestPersistenceAssembler::toDomainFromPersistence).toList();
        return PagedResult.of(items, criteria, page.getTotalElements());
    }

    private RequestPersistenceEntity updateStoredEntity(Request request) {
        var entity = requestPersistenceRepository.findWithRequestTypeById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Request %d not found".formatted(request.getId())));
        RequestPersistenceAssembler.updatePersistenceFromDomain(request, entity);
        return entity;
    }
}
