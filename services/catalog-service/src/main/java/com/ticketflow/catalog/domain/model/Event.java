package com.ticketflow.catalog.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root of the catalog: an event an organizer offers tickets for.
 * <p>
 * Instances are created only through {@link #draft} (new events) or {@link #restore}
 * (rehydration from storage), so every instance satisfies the invariants.
 */
public final class Event {

    static final int TITLE_MAX_LENGTH = 200;
    static final int DESCRIPTION_MAX_LENGTH = 5_000;

    private final EventId id;
    private final OrganizerId organizerId;
    private final String title;
    private final String description;
    private final Instant startsAt;
    private final Venue venue;
    private final EventStatus status;
    private final Instant createdAt;

    private Event(EventId id, OrganizerId organizerId, String title, String description,
                  Instant startsAt, Venue venue, EventStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.organizerId = Objects.requireNonNull(organizerId, "organizer must not be null");
        this.title = requireTitle(title);
        this.description = requireDescription(description);
        this.startsAt = Objects.requireNonNull(startsAt, "start time must not be null");
        this.venue = Objects.requireNonNull(venue, "venue must not be null");
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Event draft(OrganizerId organizerId, String title, String description,
                              Instant startsAt, Venue venue, Clock clock) {
        Instant now = clock.instant();
        if (startsAt == null || !startsAt.isAfter(now)) {
            throw new InvalidEventException("event must start in the future");
        }
        return new Event(EventId.generate(), organizerId, title, description,
                startsAt, venue, EventStatus.DRAFT, now);
    }

    public static Event restore(EventId id, OrganizerId organizerId, String title, String description,
                                Instant startsAt, Venue venue, EventStatus status, Instant createdAt) {
        return new Event(id, organizerId, title, description, startsAt, venue, status, createdAt);
    }

    private static String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidEventException("title must not be blank");
        }
        String stripped = title.strip();
        if (stripped.length() > TITLE_MAX_LENGTH) {
            throw new InvalidEventException("title must be at most " + TITLE_MAX_LENGTH + " characters");
        }
        return stripped;
    }

    private static String requireDescription(String description) {
        String value = description == null ? "" : description.strip();
        if (value.length() > DESCRIPTION_MAX_LENGTH) {
            throw new InvalidEventException("description must be at most " + DESCRIPTION_MAX_LENGTH + " characters");
        }
        return value;
    }

    public EventId id() { return id; }
    public OrganizerId organizerId() { return organizerId; }
    public String title() { return title; }
    public String description() { return description; }
    public Instant startsAt() { return startsAt; }
    public Venue venue() { return venue; }
    public EventStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
}
