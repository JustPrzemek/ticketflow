package com.ticketflow.catalog;

import org.springframework.boot.SpringApplication;

/** Runs the service locally with its infrastructure in containers — no manual setup needed. */
public class TestCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(CatalogServiceApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
