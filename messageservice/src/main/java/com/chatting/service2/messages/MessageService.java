package com.chatting.service2.messages;

import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatting.grpc.message.UserServiceGrpc;
import com.chatting.grpc.message.UserRequest;
import com.chatting.grpc.message.UserResponse;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    private final UserServiceGrpc.UserServiceBlockingStub stub;



    //@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper, UserServiceGrpc.UserServiceBlockingStub stub) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.stub = stub;
    }

    // Todo: skicka med och validera användare
    @Transactional
    public ReceiveMessageDTO saveMessage(CreateMessageDTO messageRequest) {

        if (messageRequest == null) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        // gRPC CALL --> Get username from UserService to verify user exists before message is saved in DB

//        UserRequest grpcRequest = UserRequest.newBuilder()
//                .setUsername(messageRequest.username()) // uses field från CreateMessageDTO
//                .build();
//
//        UserResponse grpcResponse = stub.validateUser(grpcRequest);
//
//        if (!grpcResponse.getExists()) {
//            throw new IllegalArgumentException("User " + messageRequest.username() + " does not exist!");
//        }

        Message message = messageMapper.toEntity(messageRequest);
        message = messageRepository.save(message);

        // Todo: Add logic to publish event to Message Queue

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
