package com.chatting.service3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponse(
        @JsonProperty("choices") List<Choice> choices
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record  Choice(
            @JsonProperty("message")  Message message
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            @JsonProperty("content") String content
    ) {}
}
