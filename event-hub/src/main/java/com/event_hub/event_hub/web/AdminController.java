package com.event_hub.event_hub.web;

import com.event_hub.event_hub.exception.BusinessException;
import com.event_hub.event_hub.exception.ResourceNotFoundException;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.service.event.EventService;
import com.event_hub.event_hub.service.registarion.RegistrationService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import com.event_hub.event_hub.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
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
        log.debug("👥 Loading all users for admin dashboard");
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", UserRole.values());
        return "admin/users";
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable UUID id, @RequestParam UserRole role,
                            RedirectAttributes redirectAttributes) {
        try {
            log.info("🔄 Changing role for user {} to {}", id, role);
            userService.changeUserRole(id, role);
            log.info("✅ Role changed successfully");
            redirectAttributes.addFlashAttribute("successMessage", "User role updated successfully");
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 User not found: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
        } catch (BusinessException ex) {
            log.warn("⚠️ Cannot change role: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable UUID id,
                              RedirectAttributes redirectAttributes) {
        try {
            log.info("🔄 Toggling status for user {}", id);
            userService.toggleUserStatus(id);
            log.info("✅ User status toggled successfully");
            redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully");
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 User not found: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
        }
        return "redirect:/users";
    }

    @GetMapping("/events")
    public String listAllEvents(Model model) {
        log.debug("📅 Loading all events for admin dashboard");
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/events";
    }

    @PostMapping("/events/delete/{id}")
    public String deleteAnyEvent(@PathVariable UUID id,
                                 @AuthenticationPrincipal AuthenticationUserDetails principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            log.info("🗑️ Admin {} deleting event {}", principal.getUsername(), id);
            eventService.deleteEvent(id, principal.getUsername());
            log.info("✅ Event deleted successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully");
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 Event not found: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found");
        } catch (BusinessException ex) {
            log.warn("⚠️ Cannot delete event: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/users/events";
    }

    @GetMapping("/registrations")
    public String listAllRegistrations(Model model) {
        log.debug("📋 Loading all registrations for admin dashboard");
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "admin/registrations";
    }
}
