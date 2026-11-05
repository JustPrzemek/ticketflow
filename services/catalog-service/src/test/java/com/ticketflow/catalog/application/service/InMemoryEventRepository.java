package com.ticketflow.catalog.application.service;

import com.ticketflow.catalog.application.port.out.EventRepository;
import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.EventId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Test double for the repository port — a fake is simpler and more honest than a mock here. */
class InMemoryEventRepository implements EventRepository {

    private final Map<EventId, Event> events = new HashMap<>();

    @Override
    public void save(Event event) {
        events.put(event.id(), event);
    }

    @Override
    public Optional<Event> findById(EventId id) {
        return Optional.ofNullable(events.get(id));
    }
}
