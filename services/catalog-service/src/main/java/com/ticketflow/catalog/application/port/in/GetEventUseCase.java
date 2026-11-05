package com.ticketflow.catalog.application.port.in;

import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.EventId;

public interface GetEventUseCase {

    /** @throws EventNotFoundException if no event with the given id exists */
    Event getEvent(EventId id);
}
