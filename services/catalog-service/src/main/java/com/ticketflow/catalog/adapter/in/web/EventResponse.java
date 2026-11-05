package com.ticketflow.catalog.adapter.in.web;

import com.ticketflow.catalog.domain.model.Event;
import java.time.Instant;

record EventResponse(
        String id,
        String organizerId,
        String title,
        String description,
        Instant startsAt,
        VenueResponse venue,
        String status,
        Instant createdAt) {

    record VenueResponse(String name, String city, String address) {
    }

    static EventResponse from(Event event) {
        return new EventResponse(
                event.id().toString(),
                event.organizerId().toString(),
                event.title(),
                event.description(),
                event.startsAt(),
                new VenueResponse(event.venue().name(), event.venue().city(), event.venue().address()),
                event.status().name(),
                event.createdAt());
    }
}
