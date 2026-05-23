package com.chatting.service2.messages;

import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository,  MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }


    @Transactional
    public ReceiveMessageDTO saveMessage(CreateMessageDTO messageRequest) {

        if (messageRequest == null) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        Message message = messageMapper.toEntity(messageRequest);
        message = messageRepository.save(message);

        return messageMapper.toReceiveDTO(message);
    }

    @Transactional
    public List<ReceiveMessageDTO> getAllMessages() {
       return messageRepository.findAllByOrderByCreatedAtAsc()
               .stream()
               .map(messageMapper::toReceiveDTO)
               .toList();
    }

}
