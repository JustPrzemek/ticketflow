package com.ticketflow.catalog.application.service;

import com.ticketflow.catalog.application.port.in.CreateEventDraftUseCase;
import com.ticketflow.catalog.application.port.in.EventNotFoundException;
import com.ticketflow.catalog.application.port.in.GetEventUseCase;
import com.ticketflow.catalog.application.port.out.EventRepository;
import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.EventId;
import com.ticketflow.catalog.domain.model.OrganizerId;
import com.ticketflow.catalog.domain.model.Venue;
import java.time.Clock;

public class EventCatalogService implements CreateEventDraftUseCase, GetEventUseCase {

    private final EventRepository events;
    private final Clock clock;

    public EventCatalogService(EventRepository events, Clock clock) {
        this.events = events;
        this.clock = clock;
    }

    @Override
    public EventId createDraft(CreateEventDraftCommand command) {
        Event event = Event.draft(
                new OrganizerId(command.organizerId()),
                command.title(),
                command.description(),
                command.startsAt(),
                new Venue(command.venueName(), command.venueCity(), command.venueAddress()),
                clock);
        events.save(event);
        return event.id();
    }

    @Override
    public Event getEvent(EventId id) {
        return events.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }
}
