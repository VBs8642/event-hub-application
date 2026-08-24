package com.event_hub.event_hub.web;

import com.event_hub.event_hub.client.BroadcastAnnouncementRequest;
import com.event_hub.event_hub.client.NotificationClient;
import com.event_hub.event_hub.exception.ResourceOwnerException;
import com.event_hub.event_hub.mapper.event.EventMapper;
import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.entity.registration.Registration;
import com.event_hub.event_hub.service.event.EventService;
import com.event_hub.event_hub.service.registarion.RegistrationService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final NotificationClient notificationClient;

    public EventController(EventService eventService, RegistrationService registrationService, NotificationClient notificationClient) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.notificationClient = notificationClient;
    }

    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        model.addAttribute("events", eventService.getPublicCatalog());
        return "events/catalog";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable UUID id, Model model) {
        try {
            Event event = eventService.getEventDetails(id);
            model.addAttribute("event", event);
        } catch (IllegalArgumentException e) {
            return "redirect:/events/catalog?error=EventNotFound";
        }
        return "events/details";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("eventDto")) {
            model.addAttribute("eventDto", new EventCreateUpdateDto());
        }
        return "events/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String createEvent(@Valid @ModelAttribute("eventDto") EventCreateUpdateDto dto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal AuthenticationUserDetails principal) {
        if (bindingResult.hasErrors()) {
            return "events/create";
        }
        eventService.createEvent(dto, principal.getUsername());
        return "redirect:/events/dashboard";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Optional<Event> eventOptional = Optional.ofNullable(eventService.getEventDetails(id));
        if (eventOptional.isEmpty()) {
            return "redirect:/events/dashboard?error=EventNotFound";
        }
        model.addAttribute("eventDto", EventMapper.toDto(eventOptional.get()));
        model.addAttribute("eventId", id);
        return "events/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String updateEvent(@PathVariable UUID id,
                              @Valid @ModelAttribute("eventDto") EventCreateUpdateDto dto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal AuthenticationUserDetails principal,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventId", id);
            return "events/edit";
        }
        try {
            eventService.updateEvent(id, dto, principal.getUsername());
        } catch (IllegalArgumentException | IllegalStateException | ResourceOwnerException ex) {
            return "redirect:/events/dashboard?error=" + ex.getMessage();
        }
        return "redirect:/events/dashboard";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String deleteEvent(@PathVariable UUID id,
                              @AuthenticationPrincipal AuthenticationUserDetails principal) {
        try {
            eventService.deleteEvent(id, principal.getUsername());
        } catch (IllegalArgumentException | IllegalStateException | ResourceOwnerException ex) {
            return "redirect:/events/dashboard?error=" + ex.getMessage();
        }
        return "redirect:/events/dashboard";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public String showDashboard(Model model, @AuthenticationPrincipal AuthenticationUserDetails principal) {
        List<Event> events = principal.getRole() == UserRole.ADMIN
                ? eventService.getAllEvents()
                : eventService.getEventsByCreator(principal.getUsername());
        model.addAttribute("userEvents", events);
        return "events/dashboard";
    }


    @PostMapping("/{eventId}/announcements")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<String> broadcastAnnouncement(
            @PathVariable UUID eventId,
            @RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal AuthenticationUserDetails principal) {
        try {
            Event event = eventService.getEventDetails(eventId);
            

            if (principal.getRole() != UserRole.ADMIN && !event.getCreator().getUsername().equals(principal.getUsername())) {
                return ResponseEntity.status(403).body("Unauthorized");
            }


            List<Registration> registrations = registrationService.getAllRegistrations();
            List<UUID> recipientUserIds = registrations.stream()
                    .filter(r -> r.getEvent().getId().equals(eventId))
                    .map(r -> r.getAttendee().getId())
                    .toList();


            BroadcastAnnouncementRequest request = new BroadcastAnnouncementRequest();
            request.setEventId(eventId);
            request.setTitle(title);
            request.setContent(content);
            request.setRecipientUserIds(recipientUserIds);

            ResponseEntity<?> response = notificationClient.broadcastAnnouncement(request);
            return ResponseEntity.ok("Announcement sent to " + recipientUserIds.size() + " attendees");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send announcement: " + e.getMessage());
        }
    }
}

