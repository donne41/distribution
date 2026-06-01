package com.chatting.service2.messages.messagequeue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MessageEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Send event to RabbitMQ when a new message is created
    public void publishMessageCreated(MessageCreatedEvent event) {
        log.info("Publishing event to RabbitMQ with message-ID: {}", event.id());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}
