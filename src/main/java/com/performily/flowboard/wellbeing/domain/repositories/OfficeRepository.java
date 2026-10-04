package com.performily.flowboard.wellbeing.domain.repositories;

import com.performily.flowboard.wellbeing.domain.model.aggregates.Office;
import java.util.List;
import java.util.Optional;

public interface OfficeRepository {
    Office save(Office office);
    Optional<Office> findById(Long id);
    List<Office> findAll();
    boolean existsByName(String name);
}
