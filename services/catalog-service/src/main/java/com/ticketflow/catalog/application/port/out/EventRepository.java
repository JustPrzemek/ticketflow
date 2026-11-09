package com.ticketflow.catalog.application.port.out;

import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.EventId;
import java.util.Optional;

public interface EventRepository {

    void save(Event event);

    Optional<Event> findById(EventId id);
}
