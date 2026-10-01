package com.performily.flowboard.workspace.domain.model.entities;

import com.performily.flowboard.workspace.domain.model.valueobjects.AssignmentChangeType;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Job Assignment Entity
 * @summary
 * One entry of the employee job history: the area and position held between
 * startDate and endDate. The current assignment has no endDate.
 *
 * @since 1.0.0
 */
public class JobAssignment {
    private Long id;
    private Area area;
    private Position position;
    private AssignmentChangeType changeType;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Opens a new job assignment.
     *
     * @param area       the assigned area
     * @param position   the assigned position
     * @param changeType the reason of the assignment
     * @param startDate  the start date
     */
    public JobAssignment(Area area, Position position, AssignmentChangeType changeType, LocalDate startDate) {
        this(null, area, position, changeType, startDate, null);
    }

    /**
     * Rebuilds an existing job assignment. Used by the persistence assemblers.
     *
     * @param id         the job assignment id
     * @param area       the assigned area
     * @param position   the assigned position
     * @param changeType the reason of the assignment
     * @param startDate  the start date
     * @param endDate    the end date, or null when current
     */
    public JobAssignment(Long id, Area area, Position position, AssignmentChangeType changeType,
                         LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.area = Objects.requireNonNull(area, "Job assignment area cannot be null");
        this.position = Objects.requireNonNull(position, "Job assignment position cannot be null");
        this.changeType = Objects.requireNonNull(changeType, "Job assignment change type cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "Job assignment start date cannot be null");
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Job assignment end date cannot be before start date");
        }
        this.endDate = endDate;
    }

    /**
     * Closes the job assignment.
     *
     * @param endDate the end date
     */
    public void close(LocalDate endDate) {
        if (!isCurrent()) {
            throw new IllegalStateException("Job assignment is already closed");
        }
        if (endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Job assignment end date cannot be null or before start date");
        }
        this.endDate = endDate;
    }

    /**
     * Checks whether this is the current assignment.
     *
     * @return true if the assignment has no end date
     */
    public boolean isCurrent() {
        return endDate == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Area getArea() {
        return area;
    }

    public Position getPosition() {
        return position;
    }

    public AssignmentChangeType getChangeType() {
        return changeType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}