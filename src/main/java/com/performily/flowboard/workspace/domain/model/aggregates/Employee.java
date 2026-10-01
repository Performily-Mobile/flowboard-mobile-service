package com.performily.flowboard.workspace.domain.model.aggregates;

import com.performily.flowboard.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.performily.flowboard.shared.domain.model.valueobjects.EmployeeId;
import com.performily.flowboard.shared.domain.model.valueobjects.FileReference;
import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.model.entities.EmployeeDocument;
import com.performily.flowboard.workspace.domain.model.entities.JobAssignment;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.model.events.DirectManagerAssignedEvent;
import com.performily.flowboard.workspace.domain.model.events.EmployeeRegisteredEvent;
import com.performily.flowboard.workspace.domain.model.events.EmployeeReinstatedEvent;
import com.performily.flowboard.workspace.domain.model.events.EmployeeTerminatedEvent;
import com.performily.flowboard.workspace.domain.model.valueobjects.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Employee Aggregate Root
 * @summary
 * Represents an employee of the organization: personal data, contract, employment
 * status, area and position, direct manager, job history and attached documents.
 *
 * Invariants checked by the aggregate:
 * - An employee cannot be its own direct manager.
 * - Only TERMINATED employees can be reinstated; reinstatement opens a new
 *   EmploymentPeriod and JobAssignment.
 * - The position must belong to the assigned area.
 * - Every area or position change closes the current JobAssignment and opens a new one.
 * - A FIXED_TERM contract requires a contract end date.
 *
 * Invariants that need other employees (unique identity document among ACTIVE
 * employees, no cycles in the hierarchy, no termination with subordinates) are
 * checked by the application service, because they need the repository.
 * The organization chart is a query built from directManagerId, not an entity.
 *
 * @since 1.0.0
 */
public class Employee extends AbstractDomainAggregateRoot<Employee> {
    private Long id;
    private PersonName name;
    private IdentityDocument identityDocument;
    private BirthDate birthDate;
    private ContactInfo contactInfo;
    private Address address;
    private ContractType contractType;
    private EmploymentPeriod employmentPeriod;
    private EmploymentStatus status;
    private TerminationDetails termination;
    private Area area;
    private Position position;
    private EmployeeId directManagerId;
    private final List<JobAssignment> jobAssignments;
    private final List<EmployeeDocument> documents;

    /**
     * Registers a new ACTIVE employee and opens its first job assignment (HIRE).
     *
     * @param name             the employee name
     * @param identityDocument the identity document
     * @param birthDate        the birth date
     * @param contactInfo      the contact information
     * @param address          the address
     * @param contractType     the contract type
     * @param employmentPeriod the employment period
     * @param area             the assigned area
     * @param position         the assigned position
     */
    public Employee(PersonName name, IdentityDocument identityDocument, BirthDate birthDate,
                    ContactInfo contactInfo, Address address, ContractType contractType,
                    EmploymentPeriod employmentPeriod, Area area, Position position) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.identityDocument = Objects.requireNonNull(identityDocument, "Identity document cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.contactInfo = Objects.requireNonNull(contactInfo, "Contact info cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.contractType = Objects.requireNonNull(contractType, "Contract type cannot be null");
        this.employmentPeriod = Objects.requireNonNull(employmentPeriod, "Employment period cannot be null");
        validateContract(contractType, employmentPeriod);
        validateJob(area, position);
        this.area = area;
        this.position = position;
        this.status = EmploymentStatus.ACTIVE;
        this.jobAssignments = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.jobAssignments.add(new JobAssignment(area, position, AssignmentChangeType.HIRE, employmentPeriod.hireDate()));
    }

    /**
     * Rebuilds an existing employee. Used by the persistence assemblers.
     *
     * @param id               the employee id
     * @param name             the employee name
     * @param identityDocument the identity document
     * @param birthDate        the birth date
     * @param contactInfo      the contact information
     * @param address          the address
     * @param contractType     the contract type
     * @param employmentPeriod the employment period
     * @param status           the employment status
     * @param termination      the termination details, or null
     * @param area             the current area
     * @param position         the current position
     * @param directManagerId  the direct manager id, or null
     * @param jobAssignments   the job history
     * @param documents        the attached documents
     */
    public Employee(Long id, PersonName name, IdentityDocument identityDocument, BirthDate birthDate,
                    ContactInfo contactInfo, Address address, ContractType contractType,
                    EmploymentPeriod employmentPeriod, EmploymentStatus status, TerminationDetails termination,
                    Area area, Position position, EmployeeId directManagerId,
                    List<JobAssignment> jobAssignments, List<EmployeeDocument> documents) {
        this.id = id;
        this.name = name;
        this.identityDocument = identityDocument;
        this.birthDate = birthDate;
        this.contactInfo = contactInfo;
        this.address = address;
        this.contractType = contractType;
        this.employmentPeriod = employmentPeriod;
        this.status = status;
        this.termination = termination;
        this.area = area;
        this.position = position;
        this.directManagerId = directManagerId;
        this.jobAssignments = new ArrayList<>(jobAssignments == null ? List.of() : jobAssignments);
        this.documents = new ArrayList<>(documents == null ? List.of() : documents);
    }

    /**
     * Updates the personal data of the employee.
     *
     * @param name        the new name
     * @param birthDate   the new birth date
     * @param contactInfo the new contact information
     * @param address     the new address
     */
    public void updatePersonalData(PersonName name, BirthDate birthDate, ContactInfo contactInfo, Address address) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date cannot be null");
        this.contactInfo = Objects.requireNonNull(contactInfo, "Contact info cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
    }

    /**
     * Changes the area and position of the employee. Closes the current job
     * assignment and opens a new one (REASSIGNMENT) on the effective date.
     *
     * @param area          the new area
     * @param position      the new position
     * @param effectiveDate the date the change takes effect
     * @throws IllegalStateException if the employee is terminated
     * @throws IllegalArgumentException if the position does not belong to the area
     */
    public void assignJob(Area area, Position position, LocalDate effectiveDate) {
        if (status == EmploymentStatus.TERMINATED) {
            throw new IllegalStateException("A terminated employee cannot be reassigned");
        }
        validateJob(area, position);
        if (effectiveDate == null) {
            throw new IllegalArgumentException("Effective date is required");
        }
        closeCurrentJobAssignment(effectiveDate);
        this.area = area;
        this.position = position;
        this.jobAssignments.add(new JobAssignment(area, position, AssignmentChangeType.REASSIGNMENT, effectiveDate));
    }

    /**
     * Assigns the direct manager of the employee.
     *
     * @param managerId the direct manager id
     */
    public void assignDirectManager(EmployeeId managerId) {
        if (managerId == null) {
            throw new IllegalArgumentException("Manager id cannot be null");
        }
        if (managerId.value().equals(this.id)) {
            throw new IllegalArgumentException("An employee cannot be its own direct manager");
        }
        this.directManagerId = managerId;
        registerDomainEvent(new DirectManagerAssignedEvent(this.id, managerId.value()));
    }

    /**
     * Removes the direct manager of the employee.
     */
    public void removeDirectManager() {
        if (!hasDirectManager()) {
            throw new IllegalStateException("Employee has no direct manager");
        }
        this.directManagerId = null;
    }

    /**
     * Terminates the employee and closes the current job assignment.
     *
     * @param termination the termination details
     * @throws IllegalStateException if the employee is already terminated
     * @throws IllegalArgumentException if the termination date is before the hire date
     */
    public void terminate(TerminationDetails termination) {
        Objects.requireNonNull(termination, "Termination details cannot be null");
        if (status == EmploymentStatus.TERMINATED) {
            throw new IllegalStateException("Employee is already terminated");
        }
        if (termination.terminationDate().isBefore(employmentPeriod.hireDate())) {
            throw new IllegalArgumentException("Termination date cannot be before the hire date");
        }
        closeCurrentJobAssignment(termination.terminationDate());
        this.termination = termination;
        this.status = EmploymentStatus.TERMINATED;
        registerDomainEvent(new EmployeeTerminatedEvent(this.id, termination.terminationDate()));
    }

    /**
     * Reinstates a TERMINATED employee. Opens a new EmploymentPeriod and a new
     * JobAssignment (REINSTATEMENT).
     *
     * @param area              the area
     * @param position          the position
     * @param reinstatementDate the reinstatement date
     * @throws IllegalStateException if the employee is not terminated
     */
    public void reinstate(Area area, Position position, LocalDate reinstatementDate) {
        if (status != EmploymentStatus.TERMINATED) {
            throw new IllegalStateException("Only terminated employees can be reinstated");
        }
        if (reinstatementDate == null) {
            throw new IllegalArgumentException("Reinstatement date is required");
        }
        if (!reinstatementDate.isAfter(termination.terminationDate())) {
            throw new IllegalArgumentException("Reinstatement date must be after the termination date");
        }
        validateJob(area, position);
        this.employmentPeriod = new EmploymentPeriod(reinstatementDate, null);
        validateContract(this.contractType, this.employmentPeriod);
        this.area = area;
        this.position = position;
        this.termination = null;
        this.status = EmploymentStatus.ACTIVE;
        this.jobAssignments.add(new JobAssignment(area, position, AssignmentChangeType.REINSTATEMENT, reinstatementDate));
        registerDomainEvent(new EmployeeReinstatedEvent(this.id, reinstatementDate));
    }

    /**
     * Suspends an ACTIVE employee.
     *
     * @throws IllegalStateException if the employee is not active
     */
    public void suspend() {
        if (status != EmploymentStatus.ACTIVE) {
            throw new IllegalStateException("Only active employees can be suspended");
        }
        this.status = EmploymentStatus.SUSPENDED;
    }

    /**
     * Attaches a document to the employee record.
     *
     * @param type the document type
     * @param file the file reference
     * @return the attached document
     */
    public EmployeeDocument attachDocument(DocumentType type, FileReference file) {
        if (type == null || file == null) {
            throw new IllegalArgumentException("Document type and file are required");
        }
        var document = new EmployeeDocument(type, file);
        this.documents.add(document);
        return document;
    }

    /**
     * Checks whether the employee is ACTIVE.
     *
     * @return true if the status is ACTIVE
     */
    public boolean isActive() {
        return status == EmploymentStatus.ACTIVE;
    }

    /**
     * Checks whether the employee has a direct manager.
     *
     * @return true if a direct manager is assigned
     */
    public boolean hasDirectManager() {
        return directManagerId != null;
    }

    /**
     * Gets the current job assignment.
     *
     * @return the assignment without end date, or null if there is none
     */
    public JobAssignment getCurrentJobAssignment() {
        return jobAssignments.stream().filter(JobAssignment::isCurrent).findFirst().orElse(null);
    }

    /**
     * Signals that this employee has just been registered and persisted.
     *
     * <p>Called by the repository adapter after the JPA identity has been assigned.</p>
     */
    public void onRegistered() {
        registerDomainEvent(EmployeeRegisteredEvent.from(this));
    }

    private void closeCurrentJobAssignment(LocalDate endDate) {
        var current = getCurrentJobAssignment();
        if (current != null) {
            current.close(endDate);
        }
    }

    private static void validateJob(Area area, Position position) {
        if (area == null || position == null) {
            throw new IllegalArgumentException("Area and position are required");
        }
        if (!area.isActive() || !position.isActive()) {
            throw new IllegalArgumentException("Area and position must be active");
        }
        if (!position.belongsTo(area)) {
            throw new IllegalArgumentException("The position must belong to the assigned area");
        }
    }

    private static void validateContract(ContractType contractType, EmploymentPeriod employmentPeriod) {
        if (contractType == ContractType.FIXED_TERM && employmentPeriod.isOpenEnded()) {
            throw new IllegalArgumentException("A FIXED_TERM contract requires a contract end date");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonName getName() {
        return name;
    }

    public String getFullName() {
        return name.getFullName();
    }

    public IdentityDocument getIdentityDocument() {
        return identityDocument;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public Address getAddress() {
        return address;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public EmploymentPeriod getEmploymentPeriod() {
        return employmentPeriod;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public TerminationDetails getTermination() {
        return termination;
    }

    public Area getArea() {
        return area;
    }

    public Position getPosition() {
        return position;
    }

    public EmployeeId getDirectManagerId() {
        return directManagerId;
    }

    public List<JobAssignment> getJobAssignments() {
        return List.copyOf(jobAssignments);
    }

    public List<EmployeeDocument> getDocuments() {
        return List.copyOf(documents);
    }
}