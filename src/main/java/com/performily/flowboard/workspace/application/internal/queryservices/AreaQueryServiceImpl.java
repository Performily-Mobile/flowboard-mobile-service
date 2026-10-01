package com.performily.flowboard.workspace.application.internal.queryservices;

import com.performily.flowboard.workspace.application.queryservices.AreaQueryService;
import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.model.queries.GetAllAreasQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetAreaByIdQuery;
import com.performily.flowboard.workspace.domain.repositories.AreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Area Query Service Impl
 * @summary
 * Application service that resolves area read queries.
 *
 * @since 1.0.0
 */
@Service
public class AreaQueryServiceImpl implements AreaQueryService {
    private final AreaRepository areaRepository;

    /**
     * Constructor.
     *
     * @param areaRepository the {@link AreaRepository} instance
     */
    public AreaQueryServiceImpl(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Override
    public Optional<Area> handle(GetAreaByIdQuery query) {
        return areaRepository.findById(query.areaId());
    }

    @Override
    public List<Area> handle(GetAllAreasQuery query) {
        return areaRepository.findAll();
    }
}