package example.service1.users;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        System.out.println("REQUEST FOR USERS, USERNAME: " + username);
        UserEntity user = userService.findUser(username);
        if(user == null) throw new UsernameNotFoundException("No user found");
        return new UserDto(
                user.getUsername(),
                user.getPassword(),
                user.getAuthAsList());
    }


}
