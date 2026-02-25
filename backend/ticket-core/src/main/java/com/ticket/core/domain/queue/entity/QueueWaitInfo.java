package com.ticket.core.domain.queue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class QueueWaitInfo {
    private final Long rank;
    private final LocalDateTime enteredAt;
}
