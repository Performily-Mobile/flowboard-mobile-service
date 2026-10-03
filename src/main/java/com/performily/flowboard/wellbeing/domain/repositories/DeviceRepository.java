package com.performily.flowboard.wellbeing.domain.repositories;

import com.performily.flowboard.wellbeing.domain.model.entities.Device;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    Device save(Device device);
    Optional<Device> findByCode(String code);
    List<Device> findAll();
    List<Device> findAllByStatus(DeviceStatus status);
    List<Device> findAllByOfficeId(Long officeId);
    boolean existsByCode(String code);
}
