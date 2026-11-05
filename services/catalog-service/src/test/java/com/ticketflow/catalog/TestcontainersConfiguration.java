package com.ticketflow.catalog;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Infrastructure for integration tests and for running the service locally with
 * {@code spring-boot:test-run}.
 * <p>
 * One container per JVM (singleton container pattern): started once, shared by every test
 * class, removed by Testcontainers when the JVM exits. Connection details are passed to Spring
 * explicitly as properties.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("mongodb-container")));

    static {
        MONGO.start();
    }

    @Bean
    DynamicPropertyRegistrar mongoProperties() {
        return registry -> registry.add("spring.mongodb.uri", () -> MONGO.getConnectionString() + "/catalog");
    }
}
