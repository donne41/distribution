package com.chatting.service2.messages.service;

import com.chatting.service2.messages.Message;
import com.chatting.service2.messages.MessageMapper;
import com.chatting.service2.messages.MessageRepository;
import com.chatting.service2.messages.dto.CreateMessageDTO;
import com.chatting.service2.messages.dto.ReceiveMessageDTO;
import com.chatting.service2.messages.messagequeue.MessageCreatedEvent;
import com.chatting.service2.messages.messagequeue.MessageEventPublisher;
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
    private final MessageEventPublisher messageEventPublisher;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper,
                          UserServiceGrpc.UserServiceBlockingStub stub, Oauth2JwtTokenService tokenService,
                          MessageEventPublisher messageEventPublisher) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.stub = stub;
        this.tokenService = tokenService;
        this.messageEventPublisher = messageEventPublisher;
    }

    @Transactional
    public ReceiveMessageDTO saveMessage(CreateMessageDTO messageRequest, Jwt jwt) {

        if (messageRequest == null) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        // Get secure username from JWT
        String currentUsername = jwt.getSubject() != null ? jwt.getSubject() : jwt.getClaimAsString("sub");
        log.debug("Service received a message for user {}", currentUsername);

        UserResponse grpcResponse = validateUserWithGrpc(currentUsername);

        Message message = messageMapper.toEntity(messageRequest);
        message.setUsername(currentUsername);
        message.setUserId(grpcResponse.getId());
        message.setName(grpcResponse.getName());

        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }

        // Save message in db
        message = messageRepository.save(message);

        // Publish message event to RabbitMQ
        publishMessageCreatedEvent(message);

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

    private UserResponse validateUserWithGrpc(String username) {
        String token = tokenService.getAccessToken();

        UserServiceGrpc.UserServiceBlockingStub authenticatedStub = this.stub
                .withDeadlineAfter(2, TimeUnit.SECONDS)
                .withCallCredentials(createCallCredentials(token));

        UserRequest grpcRequest = UserRequest.newBuilder()
                .setUsername(username)
                .build();

        try {
            UserResponse grpcResponse = authenticatedStub.validateUser(grpcRequest);
            if (!grpcResponse.getExists()) {
                throw new IllegalArgumentException ("User " + username + " does not exist!");
            }
            return grpcResponse;
        } catch (StatusRuntimeException e) {
            throw new IllegalStateException("User service validation failed due to gRPC error", e);
        }
    }

    // Create authenticated stub through CallCredentials
    private CallCredentials createCallCredentials(String token) {
        return new CallCredentials() {
            @Override
            public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier metadataApplier) {
                appExecutor.execute(() -> {
                    try {
                        Metadata headers = new Metadata();
                        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                        headers.put(authKey, "Bearer " + token);
                        metadataApplier.apply(headers);
                    } catch (Throwable e) {
                        metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                    }
                });
            }
        };
    }

    private void publishMessageCreatedEvent(Message message){
        try {
            MessageCreatedEvent event = new MessageCreatedEvent(
                    message.getId(),
                    message.getUsername(),
                    message.getContent(),
                    message.getCreatedAt()
            );

            messageEventPublisher.publishMessageCreated(event);
        } catch (Exception e) {
            log.error("Failed to publish event to RabbitMQ with message-ID: {}",message.getId(),e);
        }
    }

}
