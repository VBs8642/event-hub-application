package com.event_hub.event_hub.web;

import com.event_hub.event_hub.exception.ResourceOwnerException;
import com.event_hub.event_hub.mapper.event.EventMapper;
import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.service.event.EventService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        Optional<Event> eventOptional = Optional.ofNullable(eventService.getEventDetails(id));

        if (eventOptional.isEmpty()) {
            return "redirect:/events/catalog?error=EventNotFound";
        }

        model.addAttribute("event", eventOptional.get());
        return "events/details";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!UserRole.ADMIN.name().equals(role) && !UserRole.ORGANIZER.name().equals(role)) {
            return "redirect:/events/catalog?error=Unauthorized";
        }
        if (!model.containsAttribute("eventDto")) {
            model.addAttribute("eventDto", new EventCreateUpdateDto());
        }
        return "events/create";
    }

    @PostMapping("/create")
    public String createEvent(@Valid @ModelAttribute("eventDto") EventCreateUpdateDto dto,
                              BindingResult bindingResult,
                              HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!UserRole.ADMIN.name().equals(role) && !UserRole.ORGANIZER.name().equals(role)) {
            return "redirect:/events/catalog?error=Unauthorized";
        }
        if (bindingResult.hasErrors()) {
            return "events/create";
        }
        String username = (String) session.getAttribute("user");
        eventService.createEvent(dto, username);
        return "redirect:/events/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!UserRole.ADMIN.name().equals(role) && !UserRole.ORGANIZER.name().equals(role)) {
            return "redirect:/events/catalog?error=Unauthorized";
        }

        Optional<Event> eventOptional = Optional.ofNullable(eventService.getEventDetails(id));

        if (eventOptional.isEmpty()) {
            return "redirect:/events/dashboard?error=EventNotFound";
        }

        EventCreateUpdateDto dto = EventMapper.toDto(eventOptional.get());

        model.addAttribute("eventDto", dto);
        model.addAttribute("eventId", id);
        return "events/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable UUID id,
                              @Valid @ModelAttribute("eventDto") EventCreateUpdateDto dto,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "events/edit";
        }

        String username = (String) session.getAttribute("user");
        try {
            eventService.updateEvent(id, dto, username);
        } catch (IllegalArgumentException | IllegalStateException | ResourceOwnerException ex) {
            return "redirect:/events/dashboard?error=" + ex.getMessage();
        }

        return "redirect:/events/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable UUID id, HttpSession session) {
        String username = (String) session.getAttribute("user");
        try {
            eventService.deleteEvent(id, username);
        } catch (IllegalArgumentException | IllegalStateException | ResourceOwnerException ex) {
            return "redirect:/events/dashboard?error=" + ex.getMessage();
        }
        return "redirect:/events/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        String username = (String) session.getAttribute("user");
        model.addAttribute("userEvents", eventService.getEventsByCreator(username));
        return "events/dashboard";
    }
}
