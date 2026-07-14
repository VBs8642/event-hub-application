package com.event_hub.event_hub.web;

import com.event_hub.event_hub.model.dto.agendaItem.AgendaItemDto;
import com.event_hub.event_hub.service.agenda.AgendaItemService;
import com.event_hub.event_hub.service.event.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/agenda")
public class AgendaController {
    private final AgendaItemService agendaItemService;
    private final EventService eventService;

    public AgendaController(AgendaItemService agendaItemService, EventService eventService) {
        this.agendaItemService = agendaItemService;
        this.eventService = eventService;
    }

    @GetMapping("/manage/{eventId}")
    public String manageAgenda(@PathVariable UUID eventId, Model model) {
        model.addAttribute("event", eventService.getEventDetails(eventId));
        model.addAttribute("agendaItems", agendaItemService.getAgendaByEvent(eventId));
        if (!model.containsAttribute("agendaDto")) {
            model.addAttribute("agendaDto", new AgendaItemDto());
        }
        return "events/agenda-manage";
    }

    @PostMapping("/add/{eventId}")
    public String addAgendaItem(@PathVariable UUID eventId,
                                @Valid @ModelAttribute("agendaDto") AgendaItemDto dto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventService.getEventDetails(eventId));
            model.addAttribute("agendaItems", agendaItemService.getAgendaByEvent(eventId));
            return "events/agenda-manage";
        }

        try {
            agendaItemService.addAgendaItem(eventId, dto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("event", eventService.getEventDetails(eventId));
            model.addAttribute("agendaItems", agendaItemService.getAgendaByEvent(eventId));
            return "events/agenda-manage";
        }

        return "redirect:/agenda/manage/" + eventId;
    }

    @PostMapping("/remove/{itemId}/event/{eventId}")
    public String removeAgendaItem(@PathVariable UUID itemId, @PathVariable UUID eventId) {
        agendaItemService.removeAgendaItem(itemId);
        return "redirect:/agenda/manage/" + eventId;
    }
}
