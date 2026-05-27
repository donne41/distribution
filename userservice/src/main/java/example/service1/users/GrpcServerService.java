package example.service1.users;

import example.grpc.user.UserRequest;
import example.grpc.user.UserResponse;
import example.grpc.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

// UserService --> Acts as the server-side while MessageService is the client
@Slf4j
@GrpcService
public class GrpcServerService extends UserServiceGrpc.UserServiceImplBase{

    private final UserRepository userRepository;

    public GrpcServerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {

        String username = request.getUsername();

        if (username == null || username.isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Username cannot be null or empty")
                    .asRuntimeException());
            return;
        }

        UserResponse response;

        try {
            // Looks up user in db
            UserEntity user = userRepository.findByUserName(username);

            // If user exists, build answer with ID and userinfo
            if (user != null) {
                if (user.getId() == null || user.getName() == null || user.getUsername() == null) {
                    responseObserver.onError(Status.INTERNAL
                            .withDescription("User data is incomplete")
                            .asRuntimeException());
                    return;
                }

                log.debug("User validation successful: username={}, userId={}", username, user.getId());
                response = UserResponse.newBuilder()
                        .setExists(true)
                        .setId(user.getId())
                        .setName(user.getName())
                        .setUsername(user.getUsername())
                        .build();
            } else {
                // if not, exists = false
                response = UserResponse.newBuilder()
                        .setExists(false)
                        .build();
            }

            // Sends back answer to MessageService
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to validate user: {}", username, e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error during user validation")
                    .asRuntimeException());
        }
    }
}
