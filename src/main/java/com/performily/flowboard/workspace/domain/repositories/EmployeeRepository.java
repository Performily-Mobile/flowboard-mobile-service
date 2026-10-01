package com.performily.flowboard.workspace.domain.repositories;

import com.performily.flowboard.shared.domain.model.valueobjects.EmailAddress;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.valueobjects.EmploymentStatus;
import com.performily.flowboard.workspace.domain.model.valueobjects.IdentityDocument;

import java.util.List;
import java.util.Optional;

/**
 * Employee Repository
 * @summary
 * Workspace employee repository port.
 *
 * @since 1.0.0
 */
public interface EmployeeRepository {
    /**
     * Finds the employee with the given id.
     *
     * @param id the id
     * @return the employee, if found
     */
    Optional<Employee> findById(Long id);

    /**
     * Finds all the employees.
     *
     * @return the list of employees
     */
    List<Employee> findAll();

    /**
     * Finds the employees that match the given filters, ordered by last name.
     * Every filter is optional.
     *
     * @param search     text to search in the names or the identity document number, or null
     * @param areaId     the area id, or null
     * @param status     the {@link EmploymentStatus} instance, or null
     * @param positionId the position id, or null
     * @return the list of employees
     */
    List<Employee> findAllByFilters(String search, Long areaId, EmploymentStatus status, Long positionId);

    /**
     * Finds the employees whose direct manager is the given employee.
     *
     * @param managerId the manager id
     * @return the list of employees
     */
    List<Employee> findAllByDirectManagerId(Long managerId);

    /**
     * Finds the employees whose status is different from the given one.
     *
     * @param status the {@link EmploymentStatus} instance
     * @return the list of employees
     */
    List<Employee> findAllByStatusNot(EmploymentStatus status);

    /**
     * Saves the employee.
     *
     * @param employee the {@link Employee} instance
     * @return the saved employee
     */
    Employee save(Employee employee);

    /**
     * Checks whether an employee exists with the given id.
     *
     * @param id the id
     * @return true if it exists
     */
    boolean existsById(Long id);

    /**
     * Checks whether an employee exists with the given e-mail address.
     *
     * @param email the {@link EmailAddress} instance
     * @return true if it exists
     */
    boolean existsByEmail(EmailAddress email);

    /**
     * Checks whether another employee exists with the given e-mail address.
     *
     * @param email the {@link EmailAddress} instance
     * @param id the id
     * @return true if it exists
     */
    boolean existsByEmailAndIdIsNot(EmailAddress email, Long id);

    /**
     * Checks whether an employee with the given identity document and status exists.
     *
     * @param identityDocument the {@link IdentityDocument} instance
     * @param status the {@link EmploymentStatus} instance
     * @return true if it exists
     */
    boolean existsByIdentityDocumentAndStatus(IdentityDocument identityDocument, EmploymentStatus status);

    /**
     * Checks whether an area has employees with the given status.
     *
     * @param areaId the area id
     * @param status the {@link EmploymentStatus} instance
     * @return true if it has
     */
    boolean existsByAreaIdAndStatus(Long areaId, EmploymentStatus status);

    /**
     * Counts the employees of an area with the given status.
     *
     * @param areaId the area id
     * @param status the {@link EmploymentStatus} instance
     * @return the number of employees
     */
    long countByAreaIdAndStatus(Long areaId, EmploymentStatus status);

    /**
     * Checks whether an employee has subordinates whose status is different from the given one.
     *
     * @param managerId the manager id
     * @param status the {@link EmploymentStatus} instance
     * @return true if it has
     */
    boolean existsByDirectManagerIdAndStatusNot(Long managerId, EmploymentStatus status);
}