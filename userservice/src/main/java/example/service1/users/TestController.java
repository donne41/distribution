package example.service1.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class TestController {

    private UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/test")
    public String test(@AuthenticationPrincipal UserEntity user) {
        //String name = userService.getNameFromUsername(user.getUsername());
        String name = "Auth princiblae is not included now ";
        return "Hello " + name + "from service1!";
    }

    @GetMapping("/api/users/{username}")
    public UserDto getUser(@PathVariable String username) {
        UserEntity user = userService.findUser(username);
        if (user == null) throw new UsernameNotFoundException("No user found");
        return new UserDto(
                user.getUsername(),
                user.getPassword(),
                user.getAuthAsList(),
                user.getName());
    }

    @GetMapping("/get/token")
    public String getClientName(@AuthenticationPrincipal Jwt jwt) {
        String token = jwt.getTokenValue().toLowerCase();
        return "HELLO FROM USER, here is token: " + token;
    }

    @GetMapping("/get/users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/get/users")
    public ResponseEntity<Void> saveUser(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("roles") String roles
    ) {
        userService.saveUser(new UserDto(username, password, List.of(roles), name));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build();
    }


}
