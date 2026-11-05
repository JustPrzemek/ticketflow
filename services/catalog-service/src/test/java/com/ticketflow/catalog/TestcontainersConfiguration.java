package com.ticketflow.catalog;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Infrastructure for integration tests. Containers are Spring beans, so Spring starts them
 * before anything that depends on them and wires connection details via {@code @ServiceConnection}.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        MongoDBContainer container = new MongoDBContainer("mongo:8.0");
        // Started eagerly: the MongoDB connection details read the replica-set URL
        // as soon as the client is created, before Spring's container lifecycle kicks in.
        container.start();
        return container;
    }
}
