package com.example.file_service.extensions;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresExtension implements BeforeAllCallback, AfterAllCallback {
    private PostgreSQLContainer<?> postgres;
    private final String DOCKER_IMAGE = "postgres:latest";
    private final String DB_USERNAME = "postgres";
    private final String DB_PASSWORD = "1234";
    private final String DB_NAME = "file";
    private final Integer DB_PORT = 5432;

    @Override
    public void afterAll(ExtensionContext context) {
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        postgres = new PostgreSQLContainer<>(DOCKER_IMAGE)
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USERNAME)
                .withPassword(DB_PASSWORD);

        postgres.start();

        int mappedPort = postgres.getMappedPort(DB_PORT);

        System.setProperty("SPRING_DATASOURCE_URL", String.format("jdbc:postgresql://localhost:%d/%s", mappedPort, DB_NAME));
        System.setProperty("SPRING_DATASOURCE_USERNAME", DB_USERNAME);
        System.setProperty("SPRING_DATASOURCE_PASSWORD", DB_PASSWORD);
    }

}
