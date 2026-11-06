package com.performily.flowboard.iam.domain.model.aggregates;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "user_accounts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "employee_id"),
        @UniqueConstraint(columnNames = "username")
    }
)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "employee_id", nullable = false)
    public Long employeeId;

    @Column(nullable = false, unique = true)
    public String username;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role = Role.EMPLOYEE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.ACTIVE;

    @Column(name = "must_change_password", nullable = false)
    public boolean mustChangePassword = true;

    @Column(name = "last_sign_in_at")
    public Instant lastSignInAt;

    @Column(name = "credential_version", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    public long credentialVersion = 1;

    public enum Role {
        HR_STAFF, EMPLOYEE
    }

    public enum Status {
        ACTIVE, DISABLED
    }
}