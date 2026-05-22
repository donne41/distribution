package com.chatting.service2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Message message)) return false;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, timestamp);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
