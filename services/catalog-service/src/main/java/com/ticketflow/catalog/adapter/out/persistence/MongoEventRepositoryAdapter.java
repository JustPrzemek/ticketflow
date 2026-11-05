package com.ticketflow.catalog.adapter.out.persistence;

import com.ticketflow.catalog.adapter.out.persistence.EventDocument.VenueDocument;
import com.ticketflow.catalog.application.port.out.EventRepository;
import com.ticketflow.catalog.domain.model.Event;
import com.ticketflow.catalog.domain.model.EventId;
import com.ticketflow.catalog.domain.model.EventStatus;
import com.ticketflow.catalog.domain.model.OrganizerId;
import com.ticketflow.catalog.domain.model.Venue;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class MongoEventRepositoryAdapter implements EventRepository {

    private final EventMongoRepository repository;

    MongoEventRepositoryAdapter(EventMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Event event) {
        repository.save(toDocument(event));
    }

    @Override
    public Optional<Event> findById(EventId id) {
        return repository.findById(id.toString()).map(MongoEventRepositoryAdapter::toDomain);
    }

    private static EventDocument toDocument(Event event) {
        Venue venue = event.venue();
        return new EventDocument(
                event.id().toString(),
                event.organizerId().toString(),
                event.title(),
                event.description(),
                event.startsAt(),
                new VenueDocument(venue.name(), venue.city(), venue.address()),
                event.status().name(),
                event.createdAt());
    }

    private static Event toDomain(EventDocument document) {
        VenueDocument venue = document.venue();
        return Event.restore(
                EventId.of(document.id()),
                new OrganizerId(UUID.fromString(document.organizerId())),
                document.title(),
                document.description(),
                document.startsAt(),
                new Venue(venue.name(), venue.city(), venue.address()),
                EventStatus.valueOf(document.status()),
                document.createdAt());
    }
}
