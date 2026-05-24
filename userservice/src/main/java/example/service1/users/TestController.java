package example.service1.users;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private UserService userService;

    public TestController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/api/test")
    public String test(@AuthenticationPrincipal UserEntity user) {
        //String name = userService.getNameFromUsername(user.getUsername());
        String name = "Auth princiblae is not included now ";
        return "Hello " + name + "from service1!";
    }

    @GetMapping("/api/users/{username}")
    public UserDto getUser(@PathVariable String username){
        UserEntity user = userService.findUser(username);
        if(user == null) throw new UsernameNotFoundException("No user found");
        return new UserDto(
                user.getUsername(),
                user.getPassword(),
                user.getAuthAsList());
    }

    @GetMapping("/api/client")
    public String getClientName(@AuthenticationPrincipal Jwt jwt){
        String token = jwt.getTokenValue().toLowerCase();
        return "HELLO FROM USER, here is token: "+ token;
    }


}
