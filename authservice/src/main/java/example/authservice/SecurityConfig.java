package example.authservice;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${user.service.adress}")
    private String userServiceAdress;
    private Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public UserDetailsService userDetailsService() {
        RestClient client = RestClient.builder()
                .baseUrl(userServiceAdress)
                .build();
        log.info("-- Rest client made: {} --", client.hashCode());

        return username -> {
            log.info(" -- UserDetails Request for {}", username);
            log.info("-- service adress: {}");
            try {
                System.out.println("-- Trying RestClient -- ");
                UserDto userDto = client
                        .get()
                        .uri("/{username}", username)
                        .retrieve()
                        .body(UserDto.class);
                if (userDto == null) {
                    log.info("-- User returns null -- ");
                    System.out.println("Throwing exception");
                    throw new UsernameNotFoundException("Account did not exist");
                }
                log.info("-- Rest client has run, returning as userDetail -- ");
                //UserEntity userDto = new UserEntity("demo", "pass");
                return User.builder()
                        .username(userDto.username())
                        .password(userDto.password())
                        .roles(userDto.roles().toArray((new String[0])))
                        .build();
            } catch (Exception e) {
                System.out.println("Client Result: " + e);
                throw new UsernameNotFoundException("Kunde inte hitta användaren från UserService");
            }
        };
    }
}
