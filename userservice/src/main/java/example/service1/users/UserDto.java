package example.service1.users;

import java.util.List;

public record UserDto(String username, String password, List<String> roles) {
}
