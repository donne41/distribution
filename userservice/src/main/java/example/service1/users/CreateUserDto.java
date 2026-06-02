package example.service1.users;

import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank
        String username,
        @NotBlank
        String password) {
    public CreateUserDto(){
        this(null, null);
    }
}
