package com.ticketflow.catalog.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.ticketflow.catalog.TestcontainersConfiguration;
import com.ticketflow.catalog.application.port.out.EventRepository;
import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.OrganizerId;
import com.ticketflow.catalog.domain.model.Venue;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Persistence adapter against a real MongoDB: save and restore round trip. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
class MongoEventRepositoryAdapterIT {

    @Autowired
    EventRepository repository;

    @Test
    void savesAndRestoresEvent() {
        Event event = draftEvent();

        repository.save(event);

        assertThat(repository.findById(event.id())).hasValueSatisfying(found -> {
            assertThat(found.id()).isEqualTo(event.id());
            assertThat(found.title()).isEqualTo("Rock Night");
            assertThat(found.venue()).isEqualTo(event.venue());
            assertThat(found.startsAt()).isEqualTo(event.startsAt());
        });
    }

    private static Event draftEvent() {
        return Event.draft(
                new OrganizerId(UUID.randomUUID()),
                "Rock Night",
                "Live music",
                Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS),
                new Venue("Tauron Arena", "Kraków", "ul. Lema 7"),
                Clock.systemUTC());
    }
}
