package com.performily.flowboard.workspace.infrastructure.persistence.jpa.repositories;

import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocumentType;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.EmployeePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Employee Persistence Repository
 * @summary
 * Spring Data repository for employee persistence entities.
 *
 * @since 1.0.0
 */
@Repository
public interface EmployeePersistenceRepository extends JpaRepository<EmployeePersistenceEntity, Long> {
    /**
     * Finds the employees whose direct manager is the given employee.
     *
     * @param managerId the manager id
     * @return the list of employees
     */
    @Query("select employee from EmployeePersistenceEntity employee where employee.directManager.id = :managerId")
    List<EmployeePersistenceEntity> findAllByDirectManagerId(@Param("managerId") Long managerId);

    /**
     * Finds the employees whose status is different from the given one.
     *
     * @param status the {@link EmploymentStatus} instance
     * @return the list of employees
     */
    @Query("select employee from EmployeePersistenceEntity employee where employee.status <> :status")
    List<EmployeePersistenceEntity> findAllByStatusNot(@Param("status") EmploymentStatus status);

    /**
     * Counts the employees with the given e-mail address.
     *
     * @param email the {@link EmailAddress} instance
     * @return the number of employees
     */
    @Query("select count(employee) from EmployeePersistenceEntity employee where employee.email = :email")
    long countByEmail(@Param("email") EmailAddress email);

    /**
     * Counts the other employees with the given e-mail address.
     *
     * @param email the {@link EmailAddress} instance
     * @param id the id
     * @return the number of employees
     */
    @Query("select count(employee) from EmployeePersistenceEntity employee where employee.email = :email and employee.id <> :id")
    long countByEmailAndIdIsNot(@Param("email") EmailAddress email, @Param("id") Long id);

    /**
     * Counts the employees with the given identity document and status.
     *
     * @param type the {@link IdentityDocumentType} instance
     * @param number the number
     * @param status the {@link EmploymentStatus} instance
     * @return the number of employees
     */
    @Query("select count(employee) from EmployeePersistenceEntity employee "
            + "where employee.identityDocument.type = :type and employee.identityDocument.number = :number "
            + "and employee.status = :status")
    long countByIdentityDocumentAndStatus(@Param("type") IdentityDocumentType type,
                                          @Param("number") String number,
                                          @Param("status") EmploymentStatus status);

    /**
     * Counts the employees of an area with the given status.
     *
     * @param areaId the area id
     * @param status the {@link EmploymentStatus} instance
     * @return the number of employees
     */
    @Query("select count(employee) from EmployeePersistenceEntity employee where employee.area.id = :areaId and employee.status = :status")
    long countByAreaIdAndStatus(@Param("areaId") Long areaId, @Param("status") EmploymentStatus status);

    /**
     * Counts the subordinates of an employee whose status is different from the given one.
     *
     * @param managerId the manager id
     * @param status the {@link EmploymentStatus} instance
     * @return the number of employees
     */
    @Query("select count(employee) from EmployeePersistenceEntity employee where employee.directManager.id = :managerId and employee.status <> :status")
    long countByDirectManagerIdAndStatusNot(@Param("managerId") Long managerId, @Param("status") EmploymentStatus status);
}