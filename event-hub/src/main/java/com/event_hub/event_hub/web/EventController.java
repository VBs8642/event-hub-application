package com.event_hub.event_hub.web;

import com.event_hub.event_hub.exception.ResourceOwnerException;
import com.event_hub.event_hub.mapper.event.EventMapper;
import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.service.event.EventService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import jakarta.validation.Valid;
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

    public EventController(EventService eventService) {
        this.eventService = eventService;
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
}
