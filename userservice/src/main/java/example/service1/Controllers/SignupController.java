package example.service1.Controllers;

import example.service1.Exceptions.UsernameAlreadyExists;
import example.service1.users.CreateUserDto;
import example.service1.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Slf4j
@Controller
public class SignupController {
    private UserService userService;

    public SignupController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/signup")
    public String loadSignupPage(Model model){
        model.addAttribute("newUser", new CreateUserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String saveNewUser(@ModelAttribute("newUser") CreateUserDto newUser,
                              BindingResult result){
        log.info("-- Starting saving process --");
        if(result.hasErrors()){
            log.error("-- New user is invalid --");
            return "signup";
        }
        try {
            userService.saveUser(newUser);
            log.info("-- USER SAVED --");
        }catch (UsernameAlreadyExists e){
            log.error("-- result rejected -- ");
            result.rejectValue("username", "error.newUser", e.getMessage());
            return "signup";
        }
        return "redirect://http:localhost:8080/";
    }
}
