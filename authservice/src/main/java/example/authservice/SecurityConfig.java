package example.authservice;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Configuration
public class SecurityConfig {

    @Value("${user.service.address}")
    private String userServiceAdress;
    @Value("${bff.service.address}")
    private String bffAddress;
    private Logger log = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public UserDetailsService userDetailsService() {

        RestClient client = RestClient.builder()
                .baseUrl(userServiceAdress)
                .build();

        return username -> {
            try {
                System.out.println("-- Trying RestClient -- ");
                UserDto userDto = client
                        .get()
                        .uri("api/users/{username}", username)
                        .retrieve()
                        .body(UserDto.class);
                if (userDto == null) {
                    log.error("-- User returns null -- ");
                    throw new UsernameNotFoundException("Account did not exist");
                }
                log.info("-- Rest client has run, returning as userDetail -- ");
                //UserEntity userDto = new UserEntity("demo", "pass");
                return User.builder()
                        .username(userDto.username())
                        .password(userDto.password())
                        .roles(userDto.roles().toArray((new String[0])))
                        .build();
            } catch (RestClientException e){
                log.error("-- Failed to fetch user details --" + e.getMessage());
                throw new RuntimeException("Error fetching user details " + e.getStackTrace());
            }catch (Exception e) {
                log.error("General expetion in userDetails service.");
                throw new UsernameNotFoundException("Could not find account " + e.getStackTrace());
            }
        };
    }

}
