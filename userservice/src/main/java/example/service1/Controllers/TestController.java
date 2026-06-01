package example.service1.Controllers;

import com.google.api.Http;
import example.service1.Exceptions.UsernameAlreadyExists;
import example.service1.users.CreateUserDto;
import example.service1.users.UserDto;
import example.service1.users.UserEntity;
import example.service1.services.UserService;
import example.service1.users.UpdateUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
                null);
    }


    @PostMapping("/find/{username}")
    public ResponseEntity<String> findUser(@PathVariable String username){
        System.out.println("-- Post for user: " + username + " --");
        var userExists = userService.userExits(username);
        if(userExists){
            return new ResponseEntity<>("",HttpStatus.FOUND);
        }else return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
    }
//    @GetMapping("/account")
//    public ResponseEntity<Void> getAccountPage(Model model,
//                                 @AuthenticationPrincipal Jwt jwt){
//        String jwtusername = jwt.getClaimAsString("sub");
//
//        System.out.println("Subject: "+ jwtusername);
//        return ResponseEntity.ok()
//    }

    @PostMapping("/account")
    public ResponseEntity<?> updateAccount(@RequestBody UpdateUserDto updateUser,
                                @AuthenticationPrincipal Jwt jwt){
        try {
            userService.updateUser(updateUser, jwt);
        }catch (UsernameAlreadyExists e){
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Jwt jwt){
        userService.deleteUser(jwt);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createNewAccount(@RequestBody CreateUserDto newUser){
        try {
            userService.saveUser(newUser);
        }catch (UsernameAlreadyExists e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


}
