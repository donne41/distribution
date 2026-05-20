package example.service1.users;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private UserService userService;

    public TestController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/api/test")
    public String test(@AuthenticationPrincipal UserEntity user) {
        String name = userService.getNameFromUsername(user.getUsername());
        return "Hello " + name + "from service1!";
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
