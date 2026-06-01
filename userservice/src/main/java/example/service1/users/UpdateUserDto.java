package example.service1.users;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDto(
        @NotBlank(message = "Username cannot be blank!")
        String username, String password) {
}
