package com.performily.flowboard.request.application.internal.queryservices;

import com.performily.flowboard.request.application.queryservices.RequestQueryService;
import com.performily.flowboard.request.domain.model.aggregates.Request;
import com.performily.flowboard.request.domain.model.queries.GetPendingRequestsByApproverIdQuery;
import com.performily.flowboard.request.domain.model.queries.GetPendingRequestsForHrStaffQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestByIdQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestsByRequesterIdQuery;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Request Query Service Impl
 * @summary
 * Application service that resolves request read queries.
 *
 * @since 1.0.0
 */
@Service
public class RequestQueryServiceImpl implements RequestQueryService {
    private final RequestRepository requestRepository;

    /**
     * Constructor.
     *
     * @param requestRepository the {@link RequestRepository} instance
     */
    public RequestQueryServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Optional<Request> handle(GetRequestByIdQuery query) {
        return requestRepository.findById(query.requestId());
    }

    @Override
    public List<Request> handle(GetRequestsByRequesterIdQuery query) {
        return requestRepository.findAllByRequesterId(query.requesterId(), query.status());
    }

    @Override
    public List<Request> handle(GetPendingRequestsByApproverIdQuery query) {
        return requestRepository.findAllPendingByApproverEmployeeId(query.approverId(), query.requestTypeId());
    }

    @Override
    public List<Request> handle(GetPendingRequestsForHrStaffQuery query) {
        return requestRepository.findAllPendingForHrStaff(query.requestTypeId());
    }
}
