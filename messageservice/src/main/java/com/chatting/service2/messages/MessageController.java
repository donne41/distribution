package com.chatting.service2.messages;

import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import com.chatting.service2.messages.service.MessageService;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/test")
    public String test() {
        return "hello from Message Service! ";
    }

    @PostMapping()
    public ResponseEntity<ReceiveMessageDTO> createMessage(@RequestBody CreateMessageDTO messageRequest){
       ReceiveMessageDTO receiveMessage = messageService.saveMessage(messageRequest);
       return ResponseEntity.created(URI.create("/api/messages")).body(receiveMessage);

    }

    @GetMapping()
    public ResponseEntity<List<ReceiveMessageDTO>> getAllMessages(){
        List<ReceiveMessageDTO> receiveMessage = messageService.getAllMessages();
        return ResponseEntity.ok(receiveMessage);
    }



}
