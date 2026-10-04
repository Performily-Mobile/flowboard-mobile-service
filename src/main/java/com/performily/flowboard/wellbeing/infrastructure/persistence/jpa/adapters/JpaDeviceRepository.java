package com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceCode;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.domain.repositories.DeviceRepository;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;
import com.performily.flowboard.wellbeing.infrastructure.persistence.jpa.repositories.DevicePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaDeviceRepository implements DeviceRepository {

    private final DevicePersistenceRepository repository;

    public JpaDeviceRepository(DevicePersistenceRepository repository) {
        this.repository = repository;
    }

    private Device toDomain(DevicePersistenceEntity e) {
        return new Device(e.getId(), new DeviceCode(e.getCode()), e.getSupportedMetrics(), e.getStatus(), e.getOfficeId());
    }

    private DevicePersistenceEntity toEntity(Device d) {
        var e = new DevicePersistenceEntity();
        e.setId(d.getId());
        e.setCode(d.getCode().value());
        e.setStatus(d.getStatus());
        e.setOfficeId(d.getOfficeId());
        e.setSupportedMetrics(new HashSet<>(d.getSupportedMetrics()));
        return e;
    }

    public Device save(Device d) {
        var e = repository.save(toEntity(d));
        d.setId(e.getId());
        return d;
    }

    public Optional<Device> findByCode(String code) {
        return repository.findByCode(code).map(this::toDomain);
    }

    public List<Device> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    public List<Device> findAllByStatus(DeviceStatus status) {
        return repository.findAllByStatus(status).stream().map(this::toDomain).toList();
    }

    public List<Device> findAllByOfficeId(Long officeId) {
        return repository.findAllByOfficeId(officeId).stream().map(this::toDomain).toList();
    }

    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
}
