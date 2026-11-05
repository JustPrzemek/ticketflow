package com.ticketflow.catalog.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventTest {

    private static final Instant NOW = Instant.parse("2026-11-05T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final OrganizerId ORGANIZER = new OrganizerId(UUID.randomUUID());
    private static final Venue VENUE = new Venue("Tauron Arena", "Kraków", "ul. Lema 7");

    @Nested
    class Draft {

        @Test
        void createsDraftWithGeneratedIdAndCreationTime() {
            Instant startsAt = NOW.plusSeconds(86_400);

            Event event = Event.draft(ORGANIZER, "  Rock Night  ", "Live music", startsAt, VENUE, CLOCK);

            assertThat(event.id()).isNotNull();
            assertThat(event.status()).isEqualTo(EventStatus.DRAFT);
            assertThat(event.title()).isEqualTo("Rock Night");
            assertThat(event.startsAt()).isEqualTo(startsAt);
            assertThat(event.createdAt()).isEqualTo(NOW);
        }

        @Test
        void rejectsEventStartingInThePast() {
            assertThatThrownBy(() -> Event.draft(ORGANIZER, "Rock Night", null, NOW.minusSeconds(1), VENUE, CLOCK))
                    .isInstanceOf(InvalidEventException.class)
                    .hasMessageContaining("future");
        }

        @Test
        void rejectsEventStartingExactlyNow() {
            assertThatThrownBy(() -> Event.draft(ORGANIZER, "Rock Night", null, NOW, VENUE, CLOCK))
                    .isInstanceOf(InvalidEventException.class);
        }

        @Test
        void rejectsBlankTitle() {
            assertThatThrownBy(() -> Event.draft(ORGANIZER, "   ", null, NOW.plusSeconds(60), VENUE, CLOCK))
                    .isInstanceOf(InvalidEventException.class)
                    .hasMessageContaining("title");
        }

        @Test
        void rejectsTooLongTitle() {
            String title = "x".repeat(Event.TITLE_MAX_LENGTH + 1);

            assertThatThrownBy(() -> Event.draft(ORGANIZER, title, null, NOW.plusSeconds(60), VENUE, CLOCK))
                    .isInstanceOf(InvalidEventException.class);
        }

        @Test
        void treatsMissingDescriptionAsEmpty() {
            Event event = Event.draft(ORGANIZER, "Rock Night", null, NOW.plusSeconds(60), VENUE, CLOCK);

            assertThat(event.description()).isEmpty();
        }
    }

    @Nested
    class VenueValidation {

        @Test
        void rejectsBlankVenueFields() {
            assertThatThrownBy(() -> new Venue("", "Kraków", "ul. Lema 7"))
                    .isInstanceOf(InvalidEventException.class)
                    .hasMessageContaining("venue name");
        }
    }
}
