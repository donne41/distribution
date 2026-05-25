package example.authservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AuthService {

    @Value("${user.service.address}")
    private String userServiceAddress;

    private final RestClient userClient = RestClient.builder()
            .baseUrl(userServiceAddress)
            .defaultRequest(request ->
                    request.headers(headers ->
                            headers.setBasicAuth("auth-service-client",
                                    "$2a$10$Evh5MQr5BG/khb2lQfdbneXEyps9GZn2gZardi3mUb/kc4oEOE0qS"))
            ).build();

    public boolean usernameExists(String username) {
        System.out.println("-- Checking already exists --");

        var result = userClient.post()
                .uri("/find/{username}", username)
                .retrieve()
                .body(ResponseEntity.class);
        if (result.getStatusCode().value() == HttpStatus.FOUND.value()) {
            System.out.println("Return true");
            return true;
        } else {
            System.out.println("Return false");
            return false;
        }
    }

    public void saveNewUser(CreateUserDto user) {
        System.out.println("-- saving user --");
        UserDto userDto = new UserDto(
                user.username(),
                user.password(),
                List.of("user")
        );
        userClient.post()
                .uri("/get/users")
                .body(userDto);
    }
}
