package ru.d4nik.carparts.components.autocompass;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseContainerExtension implements Extension, BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static PostgreSQLContainer<?> postgreSqlContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (postgreSqlContainer == null) {
            postgreSqlContainer = new PostgreSQLContainer<>("postgres:13.2");
            postgreSqlContainer.start();

            System.setProperty("DB_HOST", postgreSqlContainer.getHost());
            System.setProperty("DB_PORT", String.valueOf(postgreSqlContainer.getMappedPort(5432)));
            System.setProperty("DB_NAME", String.valueOf(postgreSqlContainer.getDatabaseName()));
            System.setProperty("DB_USERNAME", String.valueOf(postgreSqlContainer.getUsername()));
            System.setProperty("DB_PASSWORD", String.valueOf(postgreSqlContainer.getPassword()));
        }
    }

    @Override
    public void close() {
        postgreSqlContainer.stop();
    }
}
