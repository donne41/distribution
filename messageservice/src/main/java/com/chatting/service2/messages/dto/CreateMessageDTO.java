package com.chatting.service2.messages.dto;

import java.time.LocalDateTime;

public record CreateMessageDTO(String username, String content, LocalDateTime createdAt) {
}
