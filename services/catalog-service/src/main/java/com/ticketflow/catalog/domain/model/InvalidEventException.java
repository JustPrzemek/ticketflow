package com.ticketflow.catalog.domain.model;

/** Thrown when an operation would violate an invariant of the {@link Event} aggregate. */
public class InvalidEventException extends RuntimeException {

    public InvalidEventException(String message) {
        super(message);
    }
}
