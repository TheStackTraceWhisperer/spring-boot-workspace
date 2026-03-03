package com.example.workspace.test.base;

import com.example.workspace.test.containers.LiquibaseMigrationRunner;
import com.example.workspace.test.containers.PostgresContainerHolder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Abstract base class for integration tests that require a PostgreSQL database
 * with Liquibase-managed schema.
 *
 * <p>Starts a shared PostgreSQL Testcontainer, runs Liquibase migrations,
 * and configures Spring datasource properties automatically.
 *
 * <p>Activates the {@code test-defaults} profile, which loads shared test
 * configuration from {@code application-test-defaults.yaml} in the
 * {@code test-fixtures} module.
 *
 * <p>Subclasses should be annotated with {@code @SpringBootTest} and can
 * use Spring's {@code JdbcTemplate}, JPA repositories, etc.
 *
 * <p>Example:
 * <pre>
 * &#64;SpringBootTest
 * class MyRepositoryIT extends AbstractDatabaseIntegrationTest {
 *
 *     &#64;Autowired
 *     private MyRepository repository;
 *
 *     &#64;Test
 *     void shouldSaveAndRetrieve() {
 *         // database is ready with schema applied
 *     }
 * }
 * </pre>
 */
@ActiveProfiles("test-defaults")
public abstract class AbstractDatabaseIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        PostgresContainerHolder.configureProperties(registry);
    }

    @BeforeAll
    static void runMigrations() {
        LiquibaseMigrationRunner.migrate(PostgresContainerHolder.getInstance());
    }
}

