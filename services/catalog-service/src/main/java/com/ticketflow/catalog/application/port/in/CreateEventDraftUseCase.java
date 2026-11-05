package com.ticketflow.catalog.application.port.in;

import com.ticketflow.catalog.domain.model.EventId;
import java.time.Instant;
import java.util.UUID;

public interface CreateEventDraftUseCase {

    EventId createDraft(CreateEventDraftCommand command);

    record CreateEventDraftCommand(
            UUID organizerId,
            String title,
            String description,
            Instant startsAt,
            String venueName,
            String venueCity,
            String venueAddress) {
    }
}
