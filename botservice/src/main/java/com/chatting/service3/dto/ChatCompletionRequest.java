package com.chatting.service3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatCompletionRequest(
       @JsonProperty("model") String model,
       @JsonProperty("messages")  List<Message> messages
) {

    public record Message(
            @JsonProperty("role")  String role,
            @JsonProperty("content") String content
    ){}

}
