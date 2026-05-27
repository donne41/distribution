package example.service1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class UserConfiguration {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(
                        csrf -> csrf.disable()
                )
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.GET, "/api/users/**", "/error", "/get/**")
                                .permitAll()
                                .requestMatchers("/signup").permitAll()
                                //.requestMatchers("/find/**").hasRole("SYSTEM")
                                .anyRequest().authenticated()
                )
                // read token relay
                .oauth2ResourceServer(oauth -> oauth.jwt(
                        Customizer.withDefaults()))
                .httpBasic(Customizer.withDefaults()
                );
        return http.build();
    }
     //TODO Krock med inlogg för demo demo och authservice inloggning med httpbasic
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService(UserRepository repository) {
//        return username -> {
//            UserEntity user = repository.findByUserName(username);
//            if (user == null) throw new UsernameNotFoundException("User not found!");
//            System.out.println("-- UserDetails in userService --");
//            System.out.printf("""
//                    Username: %s
//                    password: %s """, user.getUsername(), user.getPassword());
//            return User.withUsername(user.getUsername())
//                    .password(user.getPassword())
//                    .roles(user.getAuthAsList().toArray(new String[1]))
//                    .build();
//        };
//    }

}
