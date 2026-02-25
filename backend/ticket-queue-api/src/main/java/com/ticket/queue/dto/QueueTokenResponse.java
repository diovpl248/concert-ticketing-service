package com.ticket.queue.dto;

import com.ticket.core.domain.queue.entity.QueueStatus;

public record QueueTokenResponse(
    String token,
    QueueStatus status,
    Long position,
    Long estimatedWaitTime
) {
}
