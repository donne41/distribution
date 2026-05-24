package example.bff;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDto(
        String username,
        String password,
        List<String> roles,
        String name) {
}
