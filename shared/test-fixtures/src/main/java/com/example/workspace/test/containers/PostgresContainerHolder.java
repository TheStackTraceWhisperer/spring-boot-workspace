package com.example.workspace.test.containers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared PostgreSQL Testcontainer singleton.
 *
 * <p>Uses the singleton container pattern to reuse a single database container
 * across all tests in the JVM, significantly reducing test execution time.
 *
 * <p>Usage in a Spring Boot test:
 * <pre>
 * &#64;DynamicPropertySource
 * static void configureProperties(DynamicPropertyRegistry registry) {
 *     PostgresContainerHolder.configureProperties(registry);
 * }
 * </pre>
 */
@SuppressWarnings("deprecation") // PostgreSQLContainer deprecated in TC 2.x; no replacement yet
public final class PostgresContainerHolder {

    private static final String IMAGE = "postgres:17-alpine";

    private static final PostgreSQLContainer<?> INSTANCE =
            new PostgreSQLContainer<>(DockerImageName.parse(IMAGE))
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        INSTANCE.start();
    }

    private PostgresContainerHolder() {
    }

    /**
     * Returns the shared PostgreSQL container instance.
     */
    public static PostgreSQLContainer<?> getInstance() {
        return INSTANCE;
    }

    /**
     * Convenience method to configure Spring datasource properties
     * from the running container.
     */
    public static void configureProperties(
            org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", INSTANCE::getJdbcUrl);
        registry.add("spring.datasource.username", INSTANCE::getUsername);
        registry.add("spring.datasource.password", INSTANCE::getPassword);
        registry.add("spring.datasource.driver-class-name", INSTANCE::getDriverClassName);
    }
}

