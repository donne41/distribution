package example.service1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class UserConfiguration {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .csrf(
                        csrf -> csrf.disable()
                )
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.GET, "/api/users/**", "/error", "/api/**")
                                .permitAll()
                                .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
