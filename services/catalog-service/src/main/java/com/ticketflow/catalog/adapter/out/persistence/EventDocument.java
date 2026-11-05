package com.ticketflow.catalog.adapter.out.persistence;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("events")
record EventDocument(
        @Id String id,
        String organizerId,
        String title,
        String description,
        Instant startsAt,
        VenueDocument venue,
        String status,
        Instant createdAt) {

    record VenueDocument(String name, String city, String address) {
    }
}
