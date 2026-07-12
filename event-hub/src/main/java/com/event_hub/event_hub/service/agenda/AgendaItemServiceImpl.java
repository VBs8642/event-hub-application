package com.event_hub.event_hub.service.agenda;

import com.event_hub.event_hub.model.dto.agendaItem.AgendaItemDto;
import com.event_hub.event_hub.model.entity.agendaItem.AgendaItem;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.repository.agendaItem.AgendaItemRepository;
import com.event_hub.event_hub.repository.event.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AgendaItemServiceImpl implements AgendaItemService{
    private final AgendaItemRepository agendaItemRepository;
    private final EventRepository eventRepository;

    public AgendaItemServiceImpl(AgendaItemRepository agendaItemRepository, EventRepository eventRepository) {
        this.agendaItemRepository = agendaItemRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendaItem> getAgendaByEvent(UUID eventId) {
        return agendaItemRepository.findByEventIdOrderByDisplayOrderAscStartTimeAsc(eventId);
    }

    @Override
    @Transactional
    public void addAgendaItem(UUID eventId, AgendaItemDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event context not found"));


        if (dto.getStartTime().isBefore(event.getStartDateTime()) || dto.getEndTime().isAfter(event.getEndDateTime())) {
            throw new IllegalArgumentException("Agenda session times must fall within the overall event schedule.");
        }

        AgendaItem item = AgendaItem.builder()
                .title(dto.getTitle())
                .speaker(dto.getSpeaker())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .displayOrder(dto.getDisplayOrder())
                .event(event)
                .build();

        agendaItemRepository.save(item);

    }
    @Override
    @Transactional
    public void removeAgendaItem (UUID itemId){
        if (!agendaItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Agenda item does not exist.");
        }
        agendaItemRepository.deleteById(itemId);
    }
}
