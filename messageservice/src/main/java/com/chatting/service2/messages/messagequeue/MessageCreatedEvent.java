package com.chatting.service2.messages.messagequeue;

import java.time.LocalDateTime;

public record MessageCreatedEvent(
        Long id,
        String username,
        String content,
        LocalDateTime createdAt
) {}
