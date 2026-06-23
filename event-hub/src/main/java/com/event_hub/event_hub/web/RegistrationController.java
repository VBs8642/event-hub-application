package com.event_hub.event_hub.web;
import com.event_hub.event_hub.service.registarion.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/book")
    public String bookTickets(@RequestParam("eventId") UUID eventId,
                              @RequestParam("attendeesCount") int count,
                              Principal principal,
                              Model model) {
        try {
            registrationService.registerAttendee(eventId, principal.getName(), count);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return "redirect:/events/" + eventId + "?error=" + ex.getMessage();
        }
        return "redirect:/registrations/my-tickets";
    }

    @GetMapping("/my-tickets")
    public String showUserTickets(Model model, Principal principal) {
        model.addAttribute("bookings", registrationService.getRegistrationsByUser(principal.getName()));
        return "registrations/my-tickets";
    }

    @PostMapping("/cancel/{eventId}")
    public String cancelBooking(@PathVariable UUID eventId, Principal principal) {
        registrationService.cancelRegistration(eventId, principal.getName());
        return "redirect:/registrations/my-tickets";
    }
}
