package example.authservice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {

    public AuthController(AuthService service){
        this.service = service;
    }

    private AuthService service;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signUpPage(){
        System.out.println("-- Redirecting from controller -- ");
        return "redirect:http://localhost:8080/signup";
    }


}
