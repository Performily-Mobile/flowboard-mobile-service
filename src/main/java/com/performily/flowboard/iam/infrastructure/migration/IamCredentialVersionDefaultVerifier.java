package com.performily.flowboard.iam.infrastructure.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures the shared web SQL seed can omit credential_version.
 * Flyway runs before JPA, while Hibernate ddl-auto=update can subsequently
 * rewrite the column and discard its SQL DEFAULT. Verify the actual database
 * schema after Hibernate has completed, repairing only when necessary.
 * The operation affects schema metadata, never existing credentials or rows.
 */
@Component
public class IamCredentialVersionDefaultVerifier {
    private static final Logger log = LoggerFactory.getLogger(IamCredentialVersionDefaultVerifier.class);
    private final JdbcTemplate jdbc;

    public IamCredentialVersionDefaultVerifier(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureDefault() {
        Integer exists = jdbc.queryForObject("""
            SELECT COUNT(*) FROM information_schema.tables
            WHERE table_schema = DATABASE() AND table_name = 'user_accounts'
            """, Integer.class);
        if (exists == null || exists == 0) {
            throw new IllegalStateException("IAM table user_accounts is missing after JPA initialization");
        }
        var columns = jdbc.query("""
            SELECT DATA_TYPE, COLUMN_DEFAULT, IS_NULLABLE
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
              AND table_name = 'user_accounts'
              AND column_name = 'credential_version'
            """, (rs, rowNum) -> new ColumnInfo(
                rs.getString("DATA_TYPE"),
                rs.getString("COLUMN_DEFAULT"),
                rs.getString("IS_NULLABLE")));
        if (columns.size() != 1) {
            throw new IllegalStateException("IAM column credential_version is missing");
        }
        ColumnInfo column = columns.get(0);
        if ("bigint".equalsIgnoreCase(column.type())
            && "1".equals(column.defaultValue())
            && "NO".equalsIgnoreCase(column.nullable())) {
            return;
        }
        jdbc.execute("ALTER TABLE user_accounts MODIFY COLUMN credential_version BIGINT NOT NULL DEFAULT 1");
        log.info("IAM: credential_version configured as BIGINT NOT NULL DEFAULT 1 for shared web seed SQL");
    }

    private record ColumnInfo(String type, String defaultValue, String nullable) {}
}
