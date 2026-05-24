package example.service1;

import example.grpc.user.UserRequest;
import example.grpc.user.UserResponse;
import example.grpc.user.UserServiceGrpc;
import example.service1.users.UserRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

// UserService --> Acts as the server-side while MessageService is the client
@GrpcService
public class GrpcServerService extends UserServiceGrpc.UserServiceImplBase{

    private final UserRepository userRepository;

    public GrpcServerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        //
        String username = request.getUsername();

        // Todo: Add logic to verify user --> t.ex. userRepository.existsByUsername(username)
        boolean userExists = true; // Hard coded solution for now to verify call

        // Build gRPC-answer
        UserResponse response = UserResponse.newBuilder()
                .setExists(userExists)
                .build();

        // Sends back answer to MessageService
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
