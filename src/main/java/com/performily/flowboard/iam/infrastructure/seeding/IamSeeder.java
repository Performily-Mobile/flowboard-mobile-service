package com.performily.flowboard.iam.infrastructure.seeding;

import com.performily.flowboard.iam.domain.model.aggregates.UserAccount;
import com.performily.flowboard.iam.domain.repositories.UserAccountRepository;
import com.performily.flowboard.workspace.application.facades.WorkspaceContextFacade;
import com.performily.flowboard.workspace.domain.model.aggregates.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Creates the missing user accounts of the existing employees when the application starts.
 *
 * <p>An employee that already has an account is left untouched, so it is safe to run on every start.
 * Employees of the Human Resources area get the HR_STAFF role and the rest get EMPLOYEE. If no HR account
 * exists after seeding, the first employee becomes HR so that someone can always manage the accounts.
 * The username is the employee e-mail and the initial password comes from {@code iam.seed.default-password}.
 * Load the employees first and restart the application to seed their accounts.</p>
 */
@Component
public class IamSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IamSeeder.class);

    private final WorkspaceContextFacade workspace;
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final boolean enabled;
    private final String defaultPassword;

    public IamSeeder(WorkspaceContextFacade workspace,
                     UserAccountRepository users,
                     PasswordEncoder encoder,
                     @Value("${iam.seed.enabled:false}") boolean enabled,
                     @Value("${iam.seed.default-password:}") String defaultPassword) {
        this.workspace = workspace;
        this.users = users;
        this.encoder = encoder;
        this.enabled = enabled;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (defaultPassword == null || defaultPassword.length() < 8) {
            throw new IllegalStateException("Set IAM_SEED_DEFAULT_PASSWORD before enabling IAM_SEED_ENABLED");
        }
        List<Employee> employees = workspace.findAllActiveEmployees();
        String passwordHash = encoder.encode(defaultPassword);
        int created = 0;
        for (Employee employee : employees) {
            if (users.findByEmployeeId(employee.getId()).isPresent()) {
                continue;
            }
            String username = employee.getContactInfo().email().value();
            if (users.findByUsernameIgnoreCase(username).isPresent()) {
                log.warn("IAM seeder: username {} already exists, employee {} was skipped", username, employee.getId());
                continue;
            }
            UserAccount account = new UserAccount();
            account.employeeId = employee.getId();
            account.username = username;
            account.passwordHash = passwordHash;
            account.role = isHumanResources(employee) ? UserAccount.Role.HR_STAFF : UserAccount.Role.EMPLOYEE;
            account.mustChangePassword = false;
            users.save(account);
            created++;
        }
        // HR role is inferred from the employee area only; never promote an unrelated account.
        if (users.countByRoleAndStatus(UserAccount.Role.HR_STAFF, UserAccount.Status.ACTIVE) == 0) {
            log.warn("IAM seeder: no active HR account. Provision an HR employee explicitly.");
        }
        if (created > 0) {
            log.info("IAM seeder: {} accounts created", created);
        }
    }

    private boolean isHumanResources(Employee employee) {
        String area = employee.getArea().getName().toLowerCase(Locale.ROOT);
        return area.contains("recursos humanos") || area.contains("rrhh") || area.contains("human resources");
    }

}
