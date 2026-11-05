package com.ticketflow.catalog.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

/** Full stack: HTTP → application → MongoDB running in a real container. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
class EventApiIT {

    @Container
    @ServiceConnection
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0");

    @Autowired
    RestTestClient client;

    @Test
    void createsDraftAndReadsItBack() {
        Map<String, Object> request = validRequest(Instant.now().plus(30, ChronoUnit.DAYS));

        URI location = client.post().uri("/api/events")
                .header(EventController.ORGANIZER_HEADER, UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("DRAFT")
                .returnResult()
                .getResponseHeaders().getLocation();

        assertThat(location).isNotNull();
        client.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Rock Night")
                .jsonPath("$.venue.city").isEqualTo("Kraków");
    }

    @Test
    void rejectsEventInThePastWithProblemDetail() {
        client.post().uri("/api/events")
                .header(EventController.ORGANIZER_HEADER, UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(validRequest(Instant.now().minus(1, ChronoUnit.DAYS)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void returnsNotFoundForUnknownEvent() {
        client.get().uri("/api/events/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    private static Map<String, Object> validRequest(Instant startsAt) {
        return Map.of(
                "title", "Rock Night",
                "description", "Live music",
                "startsAt", startsAt.toString(),
                "venue", Map.of("name", "Tauron Arena", "city", "Kraków", "address", "ul. Lema 7"));
    }
}
