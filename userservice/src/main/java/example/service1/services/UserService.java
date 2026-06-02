package example.service1.services;


import example.service1.Exceptions.UsernameAlreadyExists;
import example.service1.users.CreateUserDto;
import example.service1.users.UserDto;
import example.service1.users.UserEntity;
import example.service1.users.UpdateUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class UserService {

    private UserRepository repository;
    private UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public UserEntity findUser(String username) {
        return repository.findByUserName(username);
    }

    @Transactional
    public void saveUser(CreateUserDto newUser) throws UsernameAlreadyExists {
        if (repository.existsByUserName(newUser.username())) {
            throw new UsernameAlreadyExists("Username is occupied!");
        }
        repository.save(mapper.userDtoToEntity(new UserDto(
                        newUser.username(),
                        newUser.password(),
                        List.of("user"),
                        null)
                )
        );
    }

    public UserDto findUserAndReturnDto(String username) {
        return mapper.userEntityToDto(findUser(username));
    }

    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    public void deleteUserByUserName(String userName) {
        if (repository.existsByUserName(userName)) {
            deleteUser(repository.findByUserName(userName).getId());
        }
    }

    public void deleteUser(Jwt jwt){
        repository.delete(findUser(jwt.getSubject()));
    }

    public void updateUser(UpdateUserDto updateUser, Jwt jwt){
        var User = repository.findByUserName(jwt.getSubject());
        if(User == null){
            log.debug("Error finding username with token, subject: " + jwt.getSubject());
            throw new RuntimeException("Username not found in token.");
        }
        if(!updateUser.username().isBlank()) {
            if (repository.existsByUserName(updateUser.username()))
                throw new UsernameAlreadyExists("New username is already occupied!");
            User.setUserName(updateUser.username());
        }
        if(!updateUser.password().isBlank()) User.setPassword(mapper.encodePassword(updateUser.password().trim()));
        repository.save(User);
    }


    public Boolean userExits(String username) {
        return repository.existsByUserName(username);
    }

}
