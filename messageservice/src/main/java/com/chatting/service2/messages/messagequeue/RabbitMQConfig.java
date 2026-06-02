package com.chatting.service2.messages.messagequeue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "message.exchange";
    public static final String QUEUE_NAME = "message-published";
    public static final String ROUTING_KEY = "message.created";

    public static final String BOT_QUEUE_NAME = "bot-replies";
    public static final String BOT_ROUTING_KEY = "message.bot.reply";

    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }


    // Queue for incoming messages från MessageService, with listener in BotService
    @Bean
    public Queue messageQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding messageBinding(Queue messageQueue, TopicExchange messageExchange) {
        return BindingBuilder
                .bind(messageQueue)
                .to(messageExchange)
                .with(ROUTING_KEY);
    }

    // Queue for AI-Bot replies, with listener in MessageService
    @Bean
    public Queue botReplyQueue() {
        return QueueBuilder.durable(BOT_QUEUE_NAME).build();
    }

    @Bean
    public Binding botReplyBinding(Queue botReplyQueue, TopicExchange messageExchange) {
        return BindingBuilder
                .bind(botReplyQueue)
                .to(messageExchange)
                .with(BOT_ROUTING_KEY);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
