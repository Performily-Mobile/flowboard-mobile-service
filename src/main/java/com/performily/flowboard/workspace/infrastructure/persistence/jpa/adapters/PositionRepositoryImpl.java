package com.performily.flowboard.workspace.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.repositories.PositionRepository;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers.PositionPersistenceAssembler;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.repositories.PositionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Position Repository Impl
 * @summary
 * Repository adapter that bridges the position domain repository port with Spring Data JPA.
 *
 * @since 1.0.0
 */
@Repository
public class PositionRepositoryImpl implements PositionRepository {
    private final PositionPersistenceRepository positionPersistenceRepository;

    /**
     * Constructor.
     *
     * @param positionPersistenceRepository the {@link PositionPersistenceRepository} instance
     */
    public PositionRepositoryImpl(PositionPersistenceRepository positionPersistenceRepository) {
        this.positionPersistenceRepository = positionPersistenceRepository;
    }

    @Override
    public Optional<Position> findById(Long id) {
        return positionPersistenceRepository.findById(id).map(PositionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Position> findAll() {
        return positionPersistenceRepository.findAll().stream().map(PositionPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Position> findAllByAreaId(Long areaId) {
        return positionPersistenceRepository.findAllByAreaId(areaId).stream().map(PositionPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Position save(Position position) {
        var saved = positionPersistenceRepository.save(PositionPersistenceAssembler.toPersistenceFromDomain(position));
        return PositionPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return positionPersistenceRepository.existsById(id);
    }
}