package example.service1.config;

import example.service1.users.UserEntity;
import example.service1.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class UserInitilizer implements CommandLineRunner {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserInitilizer(UserRepository repo, PasswordEncoder encoder) {
        userRepository = repo;
        passwordEncoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info(" -- RUNNING DATA FILL --");
            userRepository.save(new UserEntity("demo", passwordEncoder.encode("demo")));
        }
    }
}

