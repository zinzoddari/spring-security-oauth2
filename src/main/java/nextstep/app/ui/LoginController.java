package nextstep.app.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String loginForm() {
        return "login";
    }

    @PostMapping
    public ResponseEntity<String> login() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("success");
    }
}
