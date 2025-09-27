package com.example.file_service.extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaExtension implements BeforeAllCallback, AfterAllCallback {
    private static final String DOCKER_IMAGE_KAFKA = "confluentinc/cp-kafka:7.6.1";
    private static final String TEST_GROUP_ID = "file-service-group";
    private KafkaContainer kafka;

    @Override
    public void afterAll(ExtensionContext extensionContext) {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        kafka = new KafkaContainer(
                DockerImageName.parse(DOCKER_IMAGE_KAFKA)
        );

        kafka.start();

        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("spring.kafka.consumer.group-id", TEST_GROUP_ID);
    }
}
