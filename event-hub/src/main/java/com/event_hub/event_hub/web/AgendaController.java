package com.event_hub.event_hub.web;

import com.event_hub.event_hub.exception.BusinessException;
import com.event_hub.event_hub.exception.ResourceNotFoundException;
import com.event_hub.event_hub.model.dto.agendaItem.AgendaItemDto;
import com.event_hub.event_hub.service.agenda.AgendaItemService;
import com.event_hub.event_hub.service.event.EventService;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/agenda")
@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
public class AgendaController {
    private final AgendaItemService agendaItemService;
    private final EventService eventService;

    public AgendaController(AgendaItemService agendaItemService, EventService eventService) {
        this.agendaItemService = agendaItemService;
        this.eventService = eventService;
    }

    @GetMapping("/manage/{eventId}")
    public String manageAgenda(@PathVariable UUID eventId, Model model) {
        try {
            log.debug("📋 Loading agenda for event: {}", eventId);
            model.addAttribute("event", eventService.getEventDetails(eventId));
            model.addAttribute("agendaItems", agendaItemService.getAgendaByEvent(eventId));
            if (!model.containsAttribute("agendaDto")) {
                model.addAttribute("agendaDto", new AgendaItemDto());
            }
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 Event not found: {}", eventId);
            return "redirect:/events/dashboard?error=EventNotFound";
        }
        return "events/agenda-manage";
    }

    @PostMapping("/add/{eventId}")
    public String addAgendaItem(@PathVariable UUID eventId,
                                @Valid @ModelAttribute("agendaDto") AgendaItemDto dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventService.getEventDetails(eventId));
            model.addAttribute("agendaItems", agendaItemService.getAgendaByEvent(eventId));
            return "events/agenda-manage";
        }
        try {
            log.info("✏️ Adding agenda item to event: {}", eventId);
            agendaItemService.addAgendaItem(eventId, dto);
            log.info("✅ Agenda item added successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Agenda item added successfully!");
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 Event not found: {}", eventId);
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found");
            return "redirect:/events/dashboard";
        } catch (BusinessException ex) {
            log.warn("⚠️ Invalid agenda item: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("event", eventService.getEventDetails(eventId));
            model.addAttribute("agendaItems", agendaItemService.getAgendaByEvent(eventId));
            return "events/agenda-manage";
        }
        return "redirect:/agenda/manage/" + eventId;
    }

    @PostMapping("/remove/{itemId}/event/{eventId}")
    public String removeAgendaItem(@PathVariable UUID itemId, 
                                   @PathVariable UUID eventId,
                                   RedirectAttributes redirectAttributes) {
        try {
            log.info("🗑️ Removing agenda item: {}", itemId);
            agendaItemService.removeAgendaItem(itemId);
            log.info("✅ Agenda item removed successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Agenda item removed successfully!");
        } catch (ResourceNotFoundException ex) {
            log.warn("🔍 Agenda item not found: {}", itemId);
            redirectAttributes.addFlashAttribute("errorMessage", "Agenda item not found");
        }
        return "redirect:/agenda/manage/" + eventId;
    }
}
