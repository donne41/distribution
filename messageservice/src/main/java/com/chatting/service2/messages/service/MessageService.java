package com.chatting.service2.messages.service;

import com.chatting.service2.messages.Message;
import com.chatting.service2.messages.MessageMapper;
import com.chatting.service2.messages.MessageRepository;
import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import io.grpc.CallCredentials;
import io.grpc.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatting.grpc.message.UserServiceGrpc;
import com.chatting.grpc.message.UserRequest;
import com.chatting.grpc.message.UserResponse;

import io.grpc.Metadata;
import java.util.List;
import java.util.concurrent.Executor;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    private final UserServiceGrpc.UserServiceBlockingStub stub;
    private final Oauth2JwtTokenService tokenService;



    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper,
                          UserServiceGrpc.UserServiceBlockingStub stub, Oauth2JwtTokenService tokenService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.stub = stub;
        this.tokenService = tokenService;
    }


    @Transactional
    public ReceiveMessageDTO saveMessage(CreateMessageDTO messageRequest) {

        if (messageRequest == null) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        String token = tokenService.getAccessToken();
        UserServiceGrpc.UserServiceBlockingStub authenticatedStub = this.stub.withCallCredentials(new CallCredentials() {

            // Create authenticated stub with token through CallCredentials
            @Override
            public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier metadataApplier) {
                appExecutor.execute(() -> {
                    try {
                        Metadata headers = new Metadata();
                        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                        headers.put(authKey, "Bearer " + token);
                        metadataApplier.apply(headers);
                    }        catch (Throwable e){
                        metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                    }
                });
            }
        });

        // gRPC call
        UserRequest grpcRequest = UserRequest.newBuilder()
                .setUsername(messageRequest.username())
                .build();

        UserResponse grpcResponse = authenticatedStub.validateUser(grpcRequest);

        if (!grpcResponse.getExists()) {
            throw new IllegalArgumentException ("User " + messageRequest.username() + " does not exist!");
        }

        Message message = messageMapper.toEntity(messageRequest);

        // Get more detailed info from fields in UserService proto-file
        long userId = grpcResponse.getId();
        String name = grpcResponse.getName();

        message.setUserId(userId);
        message.setName(name);

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
