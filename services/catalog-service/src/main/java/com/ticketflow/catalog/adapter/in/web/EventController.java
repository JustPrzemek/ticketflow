package com.ticketflow.catalog.adapter.in.web;

import com.ticketflow.catalog.application.port.in.CreateEventDraftUseCase;
import com.ticketflow.catalog.application.port.in.CreateEventDraftUseCase.CreateEventDraftCommand;
import com.ticketflow.catalog.application.port.in.GetEventUseCase;
import com.ticketflow.catalog.domain.model.EventId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
class EventController {

    /** Temporary until authentication is added (week 5) — then the organizer comes from the JWT. */
    static final String ORGANIZER_HEADER = "X-Organizer-Id";

    private final CreateEventDraftUseCase createEventDraft;
    private final GetEventUseCase getEvent;

    EventController(CreateEventDraftUseCase createEventDraft, GetEventUseCase getEvent) {
        this.createEventDraft = createEventDraft;
        this.getEvent = getEvent;
    }

    @PostMapping
    ResponseEntity<EventResponse> create(@RequestHeader(ORGANIZER_HEADER) UUID organizerId,
                                         @Valid @RequestBody CreateEventRequest request) {
        EventId id = createEventDraft.createDraft(new CreateEventDraftCommand(
                organizerId,
                request.title(),
                request.description(),
                request.startsAt(),
                request.venue().name(),
                request.venue().city(),
                request.venue().address()));
        EventResponse body = EventResponse.from(getEvent.getEvent(id));
        return ResponseEntity.created(URI.create("/api/events/" + id)).body(body);
    }

    @GetMapping("/{id}")
    EventResponse get(@PathVariable UUID id) {
        return EventResponse.from(getEvent.getEvent(new EventId(id)));
    }
}
