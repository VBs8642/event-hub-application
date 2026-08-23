package com.event_hub.event_hub.web;

import com.event_hub.event_hub.service.registarion.RegistrationService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
                              Model model) {
        try {
            registrationService.registerAttendee(eventId, principal.getUsername(), count);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return "redirect:/events/" + eventId + "?error=" + ex.getMessage();
        }
        return "redirect:/registrations/my-tickets";
    }

    @GetMapping("/my-tickets")
    public String showUserTickets(Model model, @AuthenticationPrincipal AuthenticationUserDetails principal) {
        model.addAttribute("bookings", registrationService.getRegistrationsByUser(principal.getUsername()));
        return "registrations/my-tickets";
    }

    @PostMapping("/cancel/{eventId}")
    public String cancelBooking(@PathVariable UUID eventId,
                                @AuthenticationPrincipal AuthenticationUserDetails principal) {
        registrationService.cancelRegistration(eventId, principal.getUsername());
        return "redirect:/registrations/my-tickets";
    }
}
