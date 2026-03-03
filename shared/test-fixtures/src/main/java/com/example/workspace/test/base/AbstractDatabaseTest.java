package com.example.workspace.test.base;

import com.example.workspace.test.containers.LiquibaseMigrationRunner;
import com.example.workspace.test.containers.PostgresContainerHolder;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abstract base class for lightweight database tests that do not require
 * a Spring application context.
 *
 * <p>Provides direct JDBC access to a PostgreSQL Testcontainer with
 * Liquibase-managed schema. Useful for testing SQL queries, stored
 * procedures, or data access logic without Spring overhead.
 *
 * <p>Example:
 * <pre>
 * class MyQueryTest extends AbstractDatabaseTest {
 *
 *     &#64;Test
 *     void shouldExecuteQuery() throws SQLException {
 *         try (Connection conn = getConnection()) {
 *             // schema is ready, run queries directly
 *         }
 *     }
 * }
 * </pre>
 */
@SuppressWarnings("deprecation") // PostgreSQLContainer deprecated in TC 2.x; no replacement yet
public abstract class AbstractDatabaseTest {

    @BeforeAll
    static void runMigrations() {
        LiquibaseMigrationRunner.migrate(PostgresContainerHolder.getInstance());
    }

    /**
     * Returns the shared PostgreSQL container instance.
     */
    protected static PostgreSQLContainer<?> getContainer() {
        return PostgresContainerHolder.getInstance();
    }

    /**
     * Creates a new JDBC connection to the test database.
     * Callers are responsible for closing the connection.
     */
    protected static Connection getConnection() throws SQLException {
        PostgreSQLContainer<?> container = getContainer();
        return DriverManager.getConnection(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword());
    }
}

