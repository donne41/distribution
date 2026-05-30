package com.chatting.service2.messages.messagequeue;

import com.chatting.service2.messages.Message;
import com.chatting.service2.messages.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class BotReplyConsumer {

    private final MessageRepository messageRepository;
    private final MessageEventPublisher messageEventPublisher;

    public BotReplyConsumer(MessageRepository messageRepository, MessageEventPublisher messageEventPublisher) {
        this.messageRepository = messageRepository;
        this.messageEventPublisher = messageEventPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.BOT_QUEUE_NAME)
    public void consumeBotReply(MessageCreatedEvent botReplyEvent) {
        log.info("Received AI-Bot reply from RabbitMQ: {}", botReplyEvent.content());

        try {
            // New message entity for AI-Bot
            Message botMessage = new Message();
            botMessage.setUsername("ai-bot");
            botMessage.setUserId(0L);
            botMessage.setName("AI BOT");
            botMessage.setContent(botReplyEvent.content());
            botMessage.setCreatedAt(LocalDateTime.now());

            // Save bot answer in db
            botMessage = messageRepository.save(botMessage);
            log.info("AI-Bot message saved in db with ID: {}", botMessage.getId());

            // Publish event to "message-published"-queue --> routing key: message.created
            MessageCreatedEvent event = new MessageCreatedEvent(
                    botMessage.getId(),
                    botMessage.getUsername(),
                    botMessage.getContent(),
                    botMessage.getCreatedAt()
            );

           messageEventPublisher.publishMessageCreated(event);

        } catch (Exception e) {
            log.error("Failed to process and save AI-Bot reply", e);
        }
    }

}
