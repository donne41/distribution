package example.service1.config;

import example.service1.users.UserEntity;
import example.service1.services.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class UserInitilizer implements CommandLineRunner {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    Logger log = LoggerFactory.getLogger(UserInitilizer.class);

    public UserInitilizer(UserRepository repo, PasswordEncoder encoder) {
        userRepository = repo;
        passwordEncoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info(" -- RUNNING DATA FILL --");
            userRepository.save(new UserEntity(
                    "demo",
                    passwordEncoder.encode("demo"),
                    List.of("user")));
            userRepository.save(new UserEntity(
                    "auth-service-client",
                    passwordEncoder.encode("secretPassword"),
                    List.of("system")));
        }else {
            log.info(" -- NO DATA FILL WAS NECESSARY -- ");
        }
    }
}
