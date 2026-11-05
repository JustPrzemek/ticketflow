package com.ticketflow.catalog.application.port.in;

import com.ticketflow.catalog.domain.model.EventId;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(EventId id) {
        super("event " + id + " not found");
    }
}
