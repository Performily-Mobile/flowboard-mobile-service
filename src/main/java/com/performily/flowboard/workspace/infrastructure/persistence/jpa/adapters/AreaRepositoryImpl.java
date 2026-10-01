package com.performily.flowboard.workspace.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.repositories.AreaRepository;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers.AreaPersistenceAssembler;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.repositories.AreaPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Area Repository Impl
 * @summary
 * Repository adapter that bridges the area domain repository port with Spring Data JPA.
 *
 * @since 1.0.0
 */
@Repository
public class AreaRepositoryImpl implements AreaRepository {
    private final AreaPersistenceRepository areaPersistenceRepository;

    /**
     * Constructor.
     *
     * @param areaPersistenceRepository the {@link AreaPersistenceRepository} instance
     */
    public AreaRepositoryImpl(AreaPersistenceRepository areaPersistenceRepository) {
        this.areaPersistenceRepository = areaPersistenceRepository;
    }

    @Override
    public Optional<Area> findById(Long id) {
        return areaPersistenceRepository.findById(id).map(AreaPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Area> findAll() {
        return areaPersistenceRepository.findAll().stream().map(AreaPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public Area save(Area area) {
        var saved = areaPersistenceRepository.save(AreaPersistenceAssembler.toPersistenceFromDomain(area));
        return AreaPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return areaPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return areaPersistenceRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, Long id) {
        return areaPersistenceRepository.existsByNameAndIdIsNot(name, id);
    }
}