package com.ticketflow.catalog.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

record CreateEventRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5_000) String description,
        @NotNull @Future Instant startsAt,
        @NotNull @Valid VenueRequest venue) {

    record VenueRequest(
            @NotBlank String name,
            @NotBlank String city,
            @NotBlank String address) {
    }
}
