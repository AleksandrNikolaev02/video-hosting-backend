package com.example.file_service.extensions;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class MinioExtension implements BeforeAllCallback, AfterAllCallback {
    private GenericContainer<?> minio;
    private static final String DOCKER_IMAGE_MINIO = "minio/minio:latest";
    private static final String MINIO_ACCESS_KEY = "minio";
    private static final String MINIO_SECRET_KEY = "minio123";
    private static final Integer MINIO_PORT = 9000;
    private static final Integer MINIO_BIND_PORT = 19000;
    private static final String TEST_BUCKET_NAME = "uploads";

    @Override
    public void afterAll(ExtensionContext context) {
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        minio = new GenericContainer<>(
                DockerImageName.parse(DOCKER_IMAGE_MINIO)
        )
                .withEnv("MINIO_ACCESS_KEY", MINIO_ACCESS_KEY)
                .withEnv("MINIO_SECRET_KEY", MINIO_SECRET_KEY)
                .withCommand("server /data")
                .withExposedPorts(MINIO_PORT)
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd ->
                        cmd.withHostConfig(
                                new HostConfig()
                                        .withPortBindings(new PortBinding(
                                                new Ports.Binding("0.0.0.0", MINIO_BIND_PORT.toString()),
                                                new ExposedPort(MINIO_PORT)
                                        ))
                        ))
                .waitingFor(new HttpWaitStrategy()
                        .forPath("/minio/health/ready")
                        .forPort(MINIO_PORT)
                        .withStartupTimeout(Duration.ofSeconds(10)));

        minio.start();

        System.setProperty("minio.server.url", "http://" + minio.getHost() + ":" + MINIO_BIND_PORT);
        System.setProperty("minio.server.access_key", MINIO_ACCESS_KEY);
        System.setProperty("minio.server.secret_key", MINIO_SECRET_KEY);
        System.setProperty("minio.server.port", MINIO_BIND_PORT.toString());
        System.setProperty("file.dir", TEST_BUCKET_NAME);
    }
}
