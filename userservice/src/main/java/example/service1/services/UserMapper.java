package example.service1.services;

import example.service1.users.UserDto;
import example.service1.users.UserEntity;
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
                "",
                user.getAuthAsList(),
                user.getId());
    }

    public UserEntity userDtoToEntity(UserDto user) {
        return new UserEntity(user.username(),
                encoder.encode(user.password()),
                user.roles());
    }
}
