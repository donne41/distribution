package com.chatting.service3.consumer;

import com.chatting.service3.config.RestClientConfig;
import com.chatting.service3.dto.ChatCompletionRequest;
import com.chatting.service3.dto.ChatCompletionResponse;
import com.chatting.service3.dto.MessageCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class BotMessageConsumer {

private final RestClient aiRestClient;
private final RestClient messageRestClient;
private final String modelName;


    public BotMessageConsumer(RestClient aiRestClient,
                              RestClient messageRestClient,
                              @Value("${ai.api.model-name}")String modelName) {
        this.aiRestClient = aiRestClient;
        this.messageRestClient = messageRestClient;
        this.modelName = modelName;
    }


    @RabbitListener(queues = "message-published")
    public void consumeMessage(MessageCreatedEvent event) {
        log.info("Ai-Bot received a message from: {}", event.username());
        // Make sure the bot don´t answer its´own messages
        if ("ai-bot".equals(event.username())) {
            return;
        }

        // Trigger --> Answer message if content contains @bot
        if (event.content() != null && event.content().contains("@bot")) {
            String cleanPrompt = event.content().replaceFirst("@bot", "").trim();
            log.info("Generating answer to prompt: {}", cleanPrompt);

            String botAnswer = generateBotReply(cleanPrompt);
            log.info("Bot response ready: {}", botAnswer);

            log.info("Sending AI-Bot response to Message Service...");
            sendResponseToMessageService(botAnswer);

        }
    }

    private String generateBotReply(String cleanPrompt) {
        var requestPayload = new ChatCompletionRequest(
                modelName,
                List.of(
                        new ChatCompletionRequest.Message("user", cleanPrompt))
        );

        try {
            // Sends Post-call to chat/completions
            ChatCompletionResponse response = aiRestClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(ChatCompletionResponse.class);

            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                var choice = response.choices().getFirst();

                if (choice.message() != null && choice.message().content() != null) {
                    return choice.message().content();
                }
            }
        } catch (Exception e) {
            log.error("Failed to communicate with AI API", e);
        }
        return "The AI-Bot is unable to generate an answer right now.";

    }

    private void sendResponseToMessageService(String botAnswer) {
        var payload = new MessageCreatedEvent(null, "ai-bot", botAnswer, null);

        try {
            messageRestClient.post()
                    .uri("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully posted bot reply to Message Service endpoint. ");
        } catch (Exception e) {
            log.error("Failed to send request to Message Service", e);
        }

    }
}
