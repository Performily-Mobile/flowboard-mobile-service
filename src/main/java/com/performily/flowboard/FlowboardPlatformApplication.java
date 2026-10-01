package com.performily.flowboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Flowboard Platform Application
 * @summary
 * Bootstrap class for the Flowboard Platform application.
 *
 * Initializes Spring Boot auto-configuration and JPA auditing infrastructure.
 *
 * @since 1.0.0
 */
@EnableJpaAuditing
@SpringBootApplication
public class FlowboardPlatformApplication {
    /**
     * Starts the Flowboard Platform application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FlowboardPlatformApplication.class, args);
    }

}