package com.ticketflow.catalog.domain.model;

public record Venue(String name, String city, String address) {

    public Venue {
        name = requireText(name, "venue name");
        city = requireText(city, "venue city");
        address = requireText(address, "venue address");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidEventException(field + " must not be blank");
        }
        return value.strip();
    }
}
