package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.OfficeLocation;
import com.performily.flowboard.wellbeing.domain.repositories.OfficeRepository;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.OfficePersistenceEntity;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories.OfficePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOfficeRepository implements OfficeRepository {

    private final OfficePersistenceRepository repository;

    public JpaOfficeRepository(OfficePersistenceRepository repository) {
        this.repository = repository;
    }

    private Office toDomain(OfficePersistenceEntity e) {
        return new Office(e.getId(), e.getName(), e.getArea(),
                new OfficeLocation(e.getAddress(), e.getFloor(), e.getReference()), e.isActive());
    }

    private OfficePersistenceEntity toEntity(Office o) {
        var e = new OfficePersistenceEntity();
        e.setId(o.getId());
        e.setName(o.getName());
        e.setArea(o.getArea());
        e.setAddress(o.getLocation().address());
        e.setFloor(o.getLocation().floor());
        e.setReference(o.getLocation().reference());
        e.setActive(o.isActive());
        return e;
    }

    public Office save(Office o) {
        var e = repository.save(toEntity(o));
        o.setId(e.getId());
        return o;
    }

    public Optional<Office> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id).map(this::toDomain);
    }

    public List<Office> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}
