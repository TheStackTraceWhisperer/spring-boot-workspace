package com.example.workspace.test.containers;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Runs Liquibase migrations against a Testcontainer database.
 *
 * <p>This utility loads the changelogs from the {@code liquibase} module
 * (available on the classpath) and applies them to the container database.
 *
 * <p>Usage:
 * <pre>
 * LiquibaseMigrationRunner.migrate(PostgresContainerHolder.getInstance());
 * </pre>
 */
@SuppressWarnings("deprecation") // PostgreSQLContainer deprecated in TC 2.x; no replacement yet
public final class LiquibaseMigrationRunner {

    private static final String DEFAULT_CHANGELOG = "db/changelog/db.changelog-master.yaml";

    private LiquibaseMigrationRunner() {
    }

    /**
     * Runs the default changelog against the given container.
     */
    public static void migrate(PostgreSQLContainer<?> container) {
        migrate(container, DEFAULT_CHANGELOG);
    }

    /**
     * Runs a specific changelog against the given container.
     */
    public static void migrate(PostgreSQLContainer<?> container, String changelogPath) {
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword())) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            try (Liquibase liquibase = new Liquibase(
                    changelogPath,
                    new ClassLoaderResourceAccessor(),
                    database)) {
                liquibase.update((String) null);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to run Liquibase migration", e);
        }
    }
}

