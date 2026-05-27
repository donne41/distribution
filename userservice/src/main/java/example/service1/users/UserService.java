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
    private UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public String getNameFromUsername(String username) {
        UserEntity nameEntity = repository.findByUserName(username);
        if (nameEntity == null)
            throw new RuntimeException("No user found");
        return nameEntity.getName();
    }


    public UserEntity findUser(String username) {
        return repository.findByUserName(username);
    }

    public List<UserDto> getAllUsers() {
        return repository.getAllUsers().stream().map(
                mapper::userEntityToDto).toList();
    }

    public void saveUser(UserDto newUser) {
        repository.save(mapper.userDtoToEntity(newUser));
    }

    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    public void deleteUserByUserName(String userName) {
        if (repository.existsByUserName(userName)) {
            long Id = repository.findByUserName(userName).getId();
            deleteUser(Id);
        }
    }


    public Boolean userExits(String username) {
        return repository.existsByUserName(username);
    }
}
