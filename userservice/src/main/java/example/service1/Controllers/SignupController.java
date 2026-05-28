package example.service1.Controllers;

import example.service1.Exceptions.UsernameAlreadyExists;
import example.service1.services.UserMapper;
import example.service1.users.CreateUserDto;
import example.service1.services.UserService;
import example.service1.users.UserDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@Controller
public class SignupController {
    private UserService userService;

    public SignupController(UserService userService){
        this.userService = userService;
    }
    @Value("${bff.service.address}")
    private String bffAddress;


    @GetMapping("/signup")
    public String loadSignupPage(Model model){
        model.addAttribute("newUser", new CreateUserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String saveNewUser(@ModelAttribute("newUser") @Valid CreateUserDto newUser,
                              BindingResult result){
        if(result.hasErrors()){
            return "signup";
        }
        try {
            userService.saveUser(newUser);
        }catch (UsernameAlreadyExists e){
            result.rejectValue("username", "error.newUser", e.getMessage());
            return "signup";
        }
        return "redirect:" + bffAddress;
    }

    @GetMapping("/account")
    public String getAccountPage(@RequestHeader("currentuser") String username,
                                 Model model){
        var user = userService.findUserAndReturnDto(username);
        model.addAttribute("newUser", user);
        return "updateuser";
    }

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("newUser") UserDto updateUser){
        userService.updateUser(updateUser);
        log.info("USER UPDATED");
        return "redirect:" + bffAddress;
    }
}
