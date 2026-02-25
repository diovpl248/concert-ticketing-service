package com.ticket.queue.dto;

import jakarta.validation.constraints.NotNull;

public record QueueTokenRequest(
    @NotNull(message = "Concert ID is required")
    Long concertId
) {
}
