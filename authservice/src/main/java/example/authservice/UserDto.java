package example.authservice;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public record UserDto(String username, String password, List<String> roles) {
}
