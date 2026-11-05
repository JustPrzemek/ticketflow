package com.ticketflow.catalog.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

interface EventMongoRepository extends MongoRepository<EventDocument, String> {
}
