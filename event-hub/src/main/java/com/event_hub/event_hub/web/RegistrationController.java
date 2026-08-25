package com.event_hub.event_hub.web;

import com.event_hub.event_hub.exception.BusinessException;
import com.event_hub.event_hub.exception.ResourceNotFoundException;
import com.event_hub.event_hub.service.registarion.RegistrationService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
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
@RequestMapping("/registrations")
@PreAuthorize("isAuthenticated()")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/book")
    public String bookTickets(@RequestParam("eventId") UUID eventId,
                              @RequestParam("attendeesCount") int count,
                              @AuthenticationPrincipal AuthenticationUserDetails principal,
                              RedirectAttributes redirectAttributes) {
        try {
            log.info("📝 User '{}' attempting to book {} tickets for event {}", principal.getUsername(), count, eventId);
            registrationService.registerAttendee(eventId, principal.getUsername(), count);
            log.info("✅ Booking successful for user '{}'", principal.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Tickets booked successfully!");
        } catch (BusinessException ex) {
            log.warn("⚠️ Booking validation failed: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/events/" + eventId;
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 Event or user not found: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found");
            return "redirect:/events/catalog";
        } catch (IllegalArgumentException ex) {
            log.warn("❌ Invalid booking request: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/events/" + eventId;
        }
        return "redirect:/registrations/my-tickets";
    }

    @GetMapping("/my-tickets")
    public String showUserTickets(Model model, @AuthenticationPrincipal AuthenticationUserDetails principal) {
        log.debug("📋 Fetching tickets for user '{}'", principal.getUsername());
        model.addAttribute("bookings", registrationService.getRegistrationsByUser(principal.getUsername()));
        return "registrations/my-tickets";
    }

    @PostMapping("/cancel/{eventId}")
    public String cancelBooking(@PathVariable UUID eventId,
                                @AuthenticationPrincipal AuthenticationUserDetails principal,
                                RedirectAttributes redirectAttributes) {
        try {
            log.info("🗑️ User '{}' cancelling registration for event {}", principal.getUsername(), eventId);
            registrationService.cancelRegistration(eventId, principal.getUsername());
            log.info("✅ Registration cancelled successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled successfully!");
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 Registration not found: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Registration not found");
        } catch (BusinessException ex) {
            log.warn("⚠️ Cannot cancel registration: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/registrations/my-tickets";
    }
}
