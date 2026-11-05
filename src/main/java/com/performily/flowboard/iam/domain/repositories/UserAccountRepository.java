package com.performily.flowboard.iam.domain.repositories;

import com.performily.flowboard.iam.domain.model.aggregates.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsernameIgnoreCase(String username);

    Optional<UserAccount> findByEmployeeId(Long employeeId);

    long countByRoleAndStatus(UserAccount.Role role, UserAccount.Status status);
}