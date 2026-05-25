package com.chatting.service2.messages;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long userId;
    private String name;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Content cannot be blank")
    private String content;

    private LocalDateTime createdAt;


}
