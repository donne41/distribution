package example.service1.users;


import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository repository;
    private PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public String getNameFromUsername(String username) {
        UserEntity nameEntity = repository.findByUserName(username);
        if (nameEntity == null)
            throw new RuntimeException("No user found");
        return nameEntity.getName();
    }

    private UserDto userEntityToDto(UserEntity user){
        return new UserDto(user.getUsername(),
                "", user.getAuthAsList(),
                user.getName());
    }
    private UserEntity userDtoToEntity(UserDto user){
        return new UserEntity(user.name(),
                user.username(),
                encoder.encode(user.password()),
                user.roles());
    }

    public UserEntity findUser(String username) {
        return repository.findByUserName(username);
    }

    public List<UserDto> getAllUsers() {
        return repository.getAllUsers().stream().map(
                this::userEntityToDto).toList();
    }

    public void saveUser(UserDto newUser){
        repository.save(userDtoToEntity(newUser));
    }

    public void deleteUser(Long userId){
        repository.deleteById(userId);
    }
    public void deleteUserByUserName(String userName){
        if(repository.existsByUserName(userName)){
            long Id = repository.findByUserName(userName).getId();
            deleteUser(Id);
        }
    }


}
