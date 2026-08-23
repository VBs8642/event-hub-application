package com.event_hub.event_hub.web;

import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import com.event_hub.event_hub.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfile(@AuthenticationPrincipal AuthenticationUserDetails principal, Model model) {
        User user = userService.findByUsername(principal.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam(required = false) String profilePicture,
                                @AuthenticationPrincipal AuthenticationUserDetails principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getUsername());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setProfilePicture(profilePicture);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/profile";
    }
}
