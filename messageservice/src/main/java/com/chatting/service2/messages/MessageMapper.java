package com.chatting.service2.messages;

import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageMapper {


    public Message toEntity(CreateMessageDTO dto) {
       if (dto == null) {
           return null;
       }

       Message message = new Message();
       message.setUsername(dto.username());
       message.setContent(dto.content());
       message.setCreatedAt(LocalDateTime.now());

       return message;

    }

    public ReceiveMessageDTO toReceiveDTO(Message message) {
        if (message==null) {
            return null;
        }

        return new ReceiveMessageDTO(
               message.getId(),
                message.getUsername(),
                message.getContent(),
                message.getCreatedAt()

        );
    }

}
