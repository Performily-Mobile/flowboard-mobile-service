package com.performily.flowboard.iam.interfaces.rest;

import com.performily.flowboard.iam.application.internal.commandservices.IamService;
import com.performily.flowboard.iam.domain.model.aggregates.UserAccount;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class IamController {

    private final IamService iam;

    public IamController(IamService i) {
        iam = i;
    }

    public record Credentials(String username, String password) {}

    public record PasswordChange(String currentPassword, String newPassword) {}

    public record RoleChange(String role) {}

    private UserAccount current(Authentication a) {
        if (a == null || !(a.getPrincipal() instanceof UserAccount u))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return u;
    }

    private void hr(Authentication a) {
        if (current(a).role != UserAccount.Role.HR_STAFF)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private String bearer(String h) {
        if (h == null || !h.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return h.substring(7);
    }

    @PostMapping("/auth/login")
    public IamService.LoginResult login(@RequestBody Credentials c) {
        return iam.login(c.username(), c.password());
    }

    @PostMapping("/auth/change-password")
    public IamService.LoginResult change(Authentication a, @RequestHeader("Authorization") String authorization,
                                         @RequestBody PasswordChange c) {
        return iam.change(current(a), c.currentPassword(), c.newPassword(), bearer(authorization));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String h) {
        iam.logout(bearer(h));
        return ResponseEntity.noContent().build();
    }

    public record Account(
            Long id,
            Long employeeId,
            String username,
            String role,
            String status,
            boolean mustChangePassword
    ) {
        static Account of(UserAccount u) {
            return new Account(
                    u.id,
                    u.employeeId,
                    u.username,
                    u.role.name(),
                    u.status.name(),
                    u.mustChangePassword
            );
        }
    }

    @GetMapping("/accounts")
    public List<Account> accounts(Authentication a) {
        hr(a);
        return iam.all().stream().map(Account::of).toList();
    }

    @PatchMapping("/accounts/{id}/role")
    public Account role(Authentication a, @PathVariable Long id, @RequestBody RoleChange c) {
        hr(a);
        return Account.of(iam.role(id, c.role()));
    }

    @PostMapping("/accounts/{id}/reset-password")
    public Map<String, String> reset(Authentication a, @PathVariable Long id) {
        hr(a);
        return Map.of("temporaryPassword", iam.reset(id));
    }
}