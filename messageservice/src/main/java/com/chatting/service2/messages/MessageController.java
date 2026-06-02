package com.chatting.service2.messages;

import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import com.chatting.service2.messages.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping()
    public ResponseEntity<ReceiveMessageDTO> createMessage(@Valid @RequestBody CreateMessageDTO messageRequest,
                                                           @AuthenticationPrincipal Jwt jwt){

        ReceiveMessageDTO receiveMessage = messageService.saveMessage(messageRequest, jwt);
       return ResponseEntity.created(URI.create("/api/messages")).body(receiveMessage);

    }

    @GetMapping()
    public ResponseEntity<List<ReceiveMessageDTO>> getAllMessages(){
        List<ReceiveMessageDTO> receiveMessage = messageService.getAllMessages();
        return ResponseEntity.ok(receiveMessage);
    }
}
