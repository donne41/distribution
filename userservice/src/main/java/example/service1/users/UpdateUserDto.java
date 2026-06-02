package example.service1.users;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDto(
        String username, String password) {
}
