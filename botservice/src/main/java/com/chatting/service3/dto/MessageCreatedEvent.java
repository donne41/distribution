package com.chatting.service3.dto;

import java.time.LocalDateTime;

public record MessageCreatedEvent(
        Long id,
        String username,
        String content,
        LocalDateTime createdAt
) {}
