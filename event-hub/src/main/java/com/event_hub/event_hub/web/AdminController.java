package com.event_hub.event_hub.web;

import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.service.event.EventService;
import com.event_hub.event_hub.service.registarion.RegistrationService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import com.event_hub.event_hub.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final EventService eventService;
    private final RegistrationService registrationService;

    public AdminController(UserService userService, EventService eventService,
                           RegistrationService registrationService) {
        this.userService = userService;
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", UserRole.values());
        return "admin/users";
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable UUID id, @RequestParam UserRole role) {
        userService.changeUserRole(id, role);
        return "redirect:/users";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable UUID id) {
        userService.toggleUserStatus(id);
        return "redirect:/users";
    }

    @GetMapping("/events")
    public String listAllEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/events";
    }

    @PostMapping("/events/delete/{id}")
    public String deleteAnyEvent(@PathVariable UUID id,
                                 @AuthenticationPrincipal AuthenticationUserDetails principal) {
        eventService.deleteEvent(id, principal.getUsername());
        return "redirect:/users/events";
    }

    @GetMapping("/registrations")
    public String listAllRegistrations(Model model) {
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "admin/registrations";
    }
}
