package com.example.workspace.api.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot application class for {@code @WebMvcTest} sliced tests.
 * <p>
 * The main {@link com.example.workspace.api.SpringBootWorkspaceApplication} uses
 * {@code @EntityScan} and {@code @EnableJpaRepositories} which pull in JPA
 * infrastructure that is not available (or needed) in web-layer-only tests.
 * This stripped-down configuration avoids that problem.
 */
@SpringBootApplication
class ControllerTestApplication {
}

