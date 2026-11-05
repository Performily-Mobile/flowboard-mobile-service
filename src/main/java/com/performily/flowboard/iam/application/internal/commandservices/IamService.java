package com.performily.flowboard.iam.application.internal.commandservices;

import com.performily.flowboard.iam.domain.model.aggregates.UserAccount;
import com.performily.flowboard.iam.domain.model.entities.RevokedToken;
import com.performily.flowboard.iam.domain.repositories.*;
import com.performily.flowboard.iam.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class IamService {

    private final UserAccountRepository users;
    private final RevokedTokenRepository revoked;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public IamService(UserAccountRepository u, RevokedTokenRepository r, PasswordEncoder e, JwtService j) {
        users = u;
        revoked = r;
        encoder = e;
        jwt = j;
    }

    private ResponseStatusException error(HttpStatus s, String m) {
        return new ResponseStatusException(s, m);
    }

    public record LoginResult(String token, String role, Long employeeId, boolean mustChangePassword) {}

    @Transactional
    public LoginResult login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank())
            throw error(HttpStatus.BAD_REQUEST, "Username and password are required");
        UserAccount u = users.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        
        if (!encoder.matches(password, u.passwordHash))
            throw error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        
        if (u.status != UserAccount.Status.ACTIVE || !employeeActive(u.employeeId))
            throw error(HttpStatus.FORBIDDEN, "Account disabled");
        
        u.lastSignInAt = Instant.now();
        return result(u);
    }

    public LoginResult result(UserAccount u) {
        return new LoginResult(jwt.issue(u.id, u.credentialVersion), u.role.name(), u.employeeId, u.mustChangePassword);
    }

    public UserAccount require(Long id) {
        return users.findById(id).orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Account not found"));
    }

    public UserAccount valid(String token) {
        var c = jwt.verify(token);
        if (revoked.existsById(c.tokenId()))
            throw error(HttpStatus.UNAUTHORIZED, "Token revoked");
        
        UserAccount u = users.findById(c.accountId())
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Invalid token"));
        if (u.credentialVersion != c.credentialVersion())
            throw error(HttpStatus.UNAUTHORIZED, "Token invalidated");
        if (u.status != UserAccount.Status.ACTIVE || !employeeActive(u.employeeId))
            throw error(HttpStatus.UNAUTHORIZED, "Account disabled");
        
        return u;
    }

    @Transactional
    public LoginResult change(UserAccount u, String old, String next, String currentToken) {
        if (old == null || !encoder.matches(old, u.passwordHash))
            throw error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        if (next != null && encoder.matches(next, u.passwordHash))
            throw error(HttpStatus.BAD_REQUEST, "New password must differ from current password");
        
        if (!strong(next))
            throw error(HttpStatus.BAD_REQUEST, "Password must contain 8+ characters, uppercase, lowercase, digit and symbol");
        
        u.passwordHash = encoder.encode(next);
        u.credentialVersion++;
        u.mustChangePassword = false;
        users.save(u);
        var newSession = result(u);
        logout(currentToken);
        return newSession;
    }

    private boolean strong(String p) {
        return p != null 
                && p.length() >= 8 
                && p.matches(".*[A-Z].*") 
                && p.matches(".*[a-z].*") 
                && p.matches(".*[0-9].*") 
                && p.matches(".*[^a-zA-Z0-9].*");
    }

    @Transactional
    public void logout(String token) {
        var c = jwt.verify(token);
        if (!revoked.existsById(c.tokenId()))
            revoked.save(new RevokedToken(c.tokenId(), c.expiresAt()));
    }

    public List<UserAccount> all() {
        return users.findAll();
    }

    @SuppressWarnings("deprecation")
    @Transactional
    public UserAccount role(Long id, String role) {
        UserAccount u = require(id);
        UserAccount.Role target;
        
        try {
            target = UserAccount.Role.valueOf(role);
        } catch (Exception e) {
            throw error(HttpStatus.BAD_REQUEST, "Invalid role");
        }
        
        if (u.role == UserAccount.Role.HR_STAFF 
                && target != u.role 
                && u.status == UserAccount.Status.ACTIVE 
                && users.countByRoleAndStatus(UserAccount.Role.HR_STAFF, UserAccount.Status.ACTIVE) <= 1)
            throw error(HttpStatus.UNPROCESSABLE_ENTITY, "At least one active HR account required");
        
        u.role = target;
        return users.save(u);
    }

    private String temporary() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder b = new StringBuilder("Aa1!");
        for (int i = 0; i < 12; i++)
            b.append(chars.charAt(random.nextInt(chars.length())));
        return b.toString();
    }

    @SuppressWarnings("deprecation")
    @Transactional
    public String reset(Long id) {
        UserAccount u = require(id);
        if (u.status != UserAccount.Status.ACTIVE || !employeeActive(u.employeeId))
            throw error(HttpStatus.UNPROCESSABLE_ENTITY, "Account disabled");
        
        String p = temporary();
        u.passwordHash = encoder.encode(p);
        u.credentialVersion++;
        u.mustChangePassword = true;
        users.save(u);
        // Invalidate existing sessions for this account through a password version increment.
        return p;
    }

    @Transactional
    public void registered(Long id, String email) {
        if (users.findByEmployeeId(id).isPresent())
            return;
        if (users.findByUsernameIgnoreCase(email).isPresent())
            throw error(HttpStatus.CONFLICT, "Username already exists");
        
        UserAccount u = new UserAccount();
        u.employeeId = id;
        u.username = email;
        u.passwordHash = encoder.encode(temporary());
        users.save(u);
    }

    @Transactional
    public void status(Long employeeId, UserAccount.Status status) {
        users.findByEmployeeId(employeeId).ifPresent(u -> {
            u.status = status;
            users.save(u);
        });
    }

    private boolean employeeActive(Long employeeId) {
        return usersEntityStatus(employeeId);
    }

    private boolean usersEntityStatus(Long id) {
        return employeeLookup.existsActive(id);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.performily.flowboard.iam.application.facades.EmployeeLookup employeeLookup;
}