package com.chatting.service2.messages.messagequeue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "message.exchange";
    public static final String QUEUE_NAME = "message.creation.queue";
    public static final String ROUTING_KEY = "message.created";

    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

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

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
