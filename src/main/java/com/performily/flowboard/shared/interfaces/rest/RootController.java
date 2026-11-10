package com.performily.flowboard.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.util.Map;

@Hidden
@RestController
public class RootController {

    private static final Logger log = LoggerFactory.getLogger(RootController.class);

    private final DataSource dataSource;

    public RootController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public ResponseEntity<Void> root() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create("/swagger-ui.html").toString())
                .build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return ResponseEntity.ok(Map.of("status", "UP"));
            }
            log.warn("Health check failed: database connection is not valid");
        } catch (Exception e) {
            log.warn("Health check failed: {}", e.getClass().getSimpleName());
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("status", "DOWN"));
    }
}
