package com.performily.flowboard.workspace.application.internal.queryservices;

import com.performily.flowboard.workspace.application.queryservices.PositionQueryService;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.model.queries.GetAllPositionsByAreaIdQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetAllPositionsQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetPositionByIdQuery;
import com.performily.flowboard.workspace.domain.repositories.PositionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Position Query Service Impl
 * @summary
 * Application service that resolves position read queries.
 *
 * @since 1.0.0
 */
@Service
public class PositionQueryServiceImpl implements PositionQueryService {
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param positionRepository the {@link PositionRepository} instance
     */
    public PositionQueryServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public Optional<Position> handle(GetPositionByIdQuery query) {
        return positionRepository.findById(query.positionId());
    }

    @Override
    public List<Position> handle(GetAllPositionsQuery query) {
        return positionRepository.findAll();
    }

    @Override
    public List<Position> handle(GetAllPositionsByAreaIdQuery query) {
        return positionRepository.findAllByAreaId(query.areaId());
    }
}