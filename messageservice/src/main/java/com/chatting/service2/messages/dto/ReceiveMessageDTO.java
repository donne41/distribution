package com.chatting.service2.messages.dto;

import java.time.LocalDateTime;

public record ReceiveMessageDTO(
        Long id,
        String username,
        Long userId,
        String name,
        String content,
        LocalDateTime createdAt) {
}
