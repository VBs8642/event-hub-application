package com.event_hub.event_hub.web;

import com.event_hub.event_hub.model.dto.user.UserRegisterRequest;
import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("userRegisterDto")
    public UserRegisterRequest userRegisterDto() {
        return new UserRegisterRequest();
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("userRegisterDto") UserRegisterRequest registrationDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registrationDto);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.login(username, password);

            session.setAttribute("user", user.getUsername());
            return "redirect:/events/catalog";
        } catch (IllegalArgumentException e) {
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
