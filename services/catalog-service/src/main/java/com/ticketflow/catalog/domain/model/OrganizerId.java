package com.ticketflow.catalog.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrganizerId(UUID value) {

    public OrganizerId {
        Objects.requireNonNull(value, "organizer id must not be null");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
