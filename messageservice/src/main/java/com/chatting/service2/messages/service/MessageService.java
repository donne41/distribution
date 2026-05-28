package com.chatting.service2.messages.service;

import com.chatting.service2.messages.Message;
import com.chatting.service2.messages.MessageMapper;
import com.chatting.service2.messages.MessageRepository;
import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import io.grpc.CallCredentials;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatting.grpc.message.UserServiceGrpc;
import com.chatting.grpc.message.UserRequest;
import com.chatting.grpc.message.UserResponse;

import io.grpc.Metadata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
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
    public ReceiveMessageDTO saveMessage(CreateMessageDTO messageRequest, Jwt jwt) {

        if (messageRequest == null) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        // Get secure username from JWT
        String currentUsername = jwt.getSubject() != null ? jwt.getSubject() : jwt.getClaimAsString("sub");
        log.debug("Service received a message");

        Message message = messageMapper.toEntity(messageRequest);

        message.setUsername(currentUsername);
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }

        // Get token and create authenticated stub through CallCredentials
        String token = tokenService.getAccessToken();
        UserServiceGrpc.UserServiceBlockingStub authenticatedStub = this.stub
                        .withDeadlineAfter(2, TimeUnit.SECONDS)
                .withCallCredentials(new CallCredentials() {

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

        // gRPC call to validate user
        UserRequest grpcRequest = UserRequest.newBuilder()
                .setUsername(currentUsername)
                .build();

        UserResponse grpcResponse;
        try {
            grpcResponse = authenticatedStub.validateUser(grpcRequest);
        } catch (StatusRuntimeException e) {
            throw new IllegalArgumentException ("User service validation failed", e);
        }

        if (!grpcResponse.getExists()) {
            throw new IllegalArgumentException ("User " + currentUsername + " does not exist!");
        }

        // Get details from gRPC-answer and add to message
        long userId = grpcResponse.getId();
        String name = grpcResponse.getName();

        message.setUserId(userId);
        message.setName(name);

        message = messageRepository.save(message);

        // Todo: Add logic to publish event to Message Queue

        return messageMapper.toReceiveDTO(message);
    }

    @Transactional(readOnly = true)
    public List<ReceiveMessageDTO> getAllMessages(Jwt jwt) {
        
        String currentUsername = jwt.getSubject() != null ? jwt.getSubject() : jwt.getClaimAsString("sub");

        return messageRepository.findAllByUsernameOrderByCreatedAtAsc(currentUsername)
                .stream()
                .map(messageMapper::toReceiveDTO)
                .toList();
    }
}
