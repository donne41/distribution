package example.service1.users;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDto(
        @NotBlank
        String username,
        @NotBlank
        String password,
        List<String> roles
){}
