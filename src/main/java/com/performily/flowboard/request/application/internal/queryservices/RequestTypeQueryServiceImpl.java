package com.performily.flowboard.request.application.internal.queryservices;

import com.performily.flowboard.request.application.queryservices.RequestTypeQueryService;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.queries.GetAllRequestTypesQuery;
import com.performily.flowboard.request.domain.model.queries.GetRequestTypeByIdQuery;
import com.performily.flowboard.request.domain.repositories.RequestTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Request Type Query Service Impl
 * @summary
 * Application service that resolves request type read queries.
 *
 * @since 1.0.0
 */
@Service
public class RequestTypeQueryServiceImpl implements RequestTypeQueryService {
    private final RequestTypeRepository requestTypeRepository;

    /**
     * Constructor.
     *
     * @param requestTypeRepository the {@link RequestTypeRepository} instance
     */
    public RequestTypeQueryServiceImpl(RequestTypeRepository requestTypeRepository) {
        this.requestTypeRepository = requestTypeRepository;
    }

    @Override
    public Optional<RequestType> handle(GetRequestTypeByIdQuery query) {
        return requestTypeRepository.findById(query.requestTypeId());
    }

    @Override
    public List<RequestType> handle(GetAllRequestTypesQuery query) {
        return query.activeOnly()
                ? requestTypeRepository.findAllByActiveTrue()
                : requestTypeRepository.findAll();
    }
}
