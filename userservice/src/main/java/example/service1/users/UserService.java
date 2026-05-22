package example.service1.users;


import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

//    @Override
//    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity foundUser = repository.findByUserName(username);
//        if(foundUser == null)
//            throw new UsernameNotFoundException("No user found!");
//        return foundUser;
//    }

    public UserEntity findUser(String username) {
        return repository.findByUserName(username);
    }
}
