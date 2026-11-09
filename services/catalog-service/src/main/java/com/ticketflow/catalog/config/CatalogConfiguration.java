package com.ticketflow.catalog.config;

import com.ticketflow.catalog.application.port.out.EventRepository;
import com.ticketflow.catalog.application.service.EventCatalogService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the framework-free application core into Spring.
 * The domain and application layers stay independent of Spring (verified by ArchUnit).
 */
@Configuration(proxyBeanMethods = false)
class CatalogConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    EventCatalogService eventCatalogService(EventRepository eventRepository, Clock clock) {
        return new EventCatalogService(eventRepository, clock);
    }
}
