package com.performily.flowboard.workspace.infrastructure.persistence.jpa.assemblers;

import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import com.performily.flowboard.workspace.domain.model.valueobjects.*;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.embeddables.*;
import com.performily.flowboard.workspace.infrastructure.persistence.jpa.entities.EmployeePersistenceEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Employee Persistence Assembler
 * @summary
 * Static assembler between employee domain and persistence representations.
 *
 * @since 1.0.0
 */
public final class EmployeePersistenceAssembler {
    private EmployeePersistenceAssembler() {
    }

    /**
     * Converts a persistence entity to its domain representation.
     *
     * @param entity the {@link EmployeePersistenceEntity} instance
     * @return the domain object, or null when the entity is null
     */
    public static Employee toDomainFromPersistence(EmployeePersistenceEntity entity) {
        if (entity == null) return null;
        var name = entity.getName();
        var identityDocument = entity.getIdentityDocument();
        var address = entity.getAddress();
        var period = entity.getEmploymentPeriod();
        var termination = entity.getTermination();
        return new Employee(
                entity.getId(),
                new PersonName(name.getFirstName(), name.getLastName()),
                new IdentityDocument(identityDocument.getType(), identityDocument.getNumber()),
                entity.getBirthDate(),
                new ContactInfo(entity.getEmail(), entity.getPhoneNumber()),
                new Address(address.getStreet(), address.getDistrict(), address.getProvince(), address.getDepartment()),
                entity.getContractType(),
                new EmploymentPeriod(period.getHireDate(), period.getContractEndDate()),
                entity.getStatus(),
                termination == null || termination.getTerminationDate() == null
                        ? null
                        : new TerminationDetails(termination.getReason(), termination.getTerminationDate()),
                AreaPersistenceAssembler.toDomainFromPersistence(entity.getArea()),
                PositionPersistenceAssembler.toDomainFromPersistence(entity.getPosition()),
                entity.getDirectManager() == null ? null : new EmployeeId(entity.getDirectManager().getId()),
                entity.getJobAssignments().stream()
                        .map(JobAssignmentPersistenceAssembler::toDomainFromPersistence)
                        .toList(),
                entity.getDocuments().stream()
                        .map(EmployeeDocumentPersistenceAssembler::toDomainFromPersistence)
                        .toList());
    }

    /**
     * Converts a domain object to its persistence representation.
     *
     * @param employee the {@link Employee} instance
     * @return the persistence entity, or null when the domain object is null
     */
    public static EmployeePersistenceEntity toPersistenceFromDomain(Employee employee) {
        if (employee == null) return null;
        var entity = new EmployeePersistenceEntity();
        if (employee.getId() != null) {
            entity.setId(employee.getId());
        }
        var name = employee.getName();
        var identityDocument = employee.getIdentityDocument();
        var address = employee.getAddress();
        var period = employee.getEmploymentPeriod();
        var termination = employee.getTermination();
        entity.setName(new PersonNamePersistenceEmbeddable(name.firstName(), name.lastName()));
        entity.setIdentityDocument(new IdentityDocumentPersistenceEmbeddable(identityDocument.type(), identityDocument.number()));
        entity.setBirthDate(employee.getBirthDate());
        entity.setEmail(employee.getContactInfo().email());
        entity.setPhoneNumber(employee.getContactInfo().phoneNumber());
        entity.setAddress(new AddressPersistenceEmbeddable(
                address.street(), address.district(), address.province(), address.department()));
        entity.setContractType(employee.getContractType());
        entity.setEmploymentPeriod(new EmploymentPeriodPersistenceEmbeddable(period.hireDate(), period.contractEndDate()));
        entity.setStatus(employee.getStatus());
        entity.setTermination(termination == null
                ? null
                : new TerminationDetailsPersistenceEmbeddable(termination.reason(), termination.terminationDate()));
        entity.setArea(AreaPersistenceAssembler.toPersistenceFromDomain(employee.getArea()));
        entity.setPosition(PositionPersistenceAssembler.toPersistenceFromDomain(employee.getPosition()));
        if (employee.getDirectManagerId() != null) {
            var directManager = new EmployeePersistenceEntity();
            directManager.setId(employee.getDirectManagerId().value());
            entity.setDirectManager(directManager);
        }
        entity.setJobAssignments(employee.getJobAssignments().stream()
                .map(jobAssignment -> JobAssignmentPersistenceAssembler.toPersistenceFromDomain(jobAssignment, entity))
                .collect(Collectors.toCollection(ArrayList::new)));
        entity.setDocuments(employee.getDocuments().stream()
                .map(document -> EmployeeDocumentPersistenceAssembler.toPersistenceFromDomain(document, entity))
                .collect(Collectors.toCollection(ArrayList::new)));
        return entity;
    }
}