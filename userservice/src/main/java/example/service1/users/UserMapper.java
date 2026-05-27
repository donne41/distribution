package example.service1.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private PasswordEncoder encoder;

    public UserMapper(PasswordEncoder encoder){
        this.encoder = encoder;
    }

    public UserDto userEntityToDto(UserEntity user) {
        return new UserDto(user.getUsername(),
                "", user.getAuthAsList(),
                user.getName());
    }

    public UserEntity userDtoToEntity(UserDto user) {
        return new UserEntity(user.name(),
                user.username(),
                encoder.encode(user.password()),
                user.roles());
    }
}
