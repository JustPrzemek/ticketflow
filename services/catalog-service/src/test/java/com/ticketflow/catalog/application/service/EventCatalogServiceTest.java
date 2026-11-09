package com.ticketflow.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ticketflow.catalog.application.port.in.CreateEventDraftUseCase.CreateEventDraftCommand;
import com.ticketflow.catalog.application.port.in.EventNotFoundException;
import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.EventId;
import com.ticketflow.catalog.domain.model.EventStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EventCatalogServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-05T12:00:00Z");

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final EventCatalogService service =
            new EventCatalogService(repository, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void createdDraftCanBeRetrieved() {
        UUID organizerId = UUID.randomUUID();

        EventId id = service.createDraft(new CreateEventDraftCommand(
                organizerId, "Rock Night", "Live music", NOW.plusSeconds(3_600),
                "Tauron Arena", "Kraków", "ul. Lema 7"));

        Event event = service.getEvent(id);
        assertThat(event.status()).isEqualTo(EventStatus.DRAFT);
        assertThat(event.organizerId().value()).isEqualTo(organizerId);
        assertThat(event.venue().city()).isEqualTo("Kraków");
    }

    @Test
    void unknownEventIsReportedAsNotFound() {
        EventId unknown = EventId.generate();

        assertThatThrownBy(() -> service.getEvent(unknown))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(unknown.toString());
    }
}
