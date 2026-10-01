package com.performily.flowboard.workspace.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.PositionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Position Persistence Repository
 * @summary
 * Spring Data repository for position persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface PositionPersistenceRepository extends JpaRepository<PositionPersistenceEntity, Long> {
    /**
     * Finds the positions of an area.
     *
     * @param areaId the area id
     * @return the list of positions
     */
    @Query("select p from PositionPersistenceEntity p where p.area.id = :areaId")
    List<PositionPersistenceEntity> findAllByAreaId(@Param("areaId") Long areaId);
}