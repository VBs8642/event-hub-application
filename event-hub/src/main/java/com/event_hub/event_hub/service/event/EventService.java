package com.event_hub.event_hub.service.event;

import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.entity.event.Event;

import java.util.List;
import java.util.UUID;

public interface EventService {
    Event createEvent(EventCreateUpdateDto dto, String username);
    Event updateEvent(UUID eventId, EventCreateUpdateDto dto, String username);
    void deleteEvent(UUID eventId, String username);
    Event getEventDetails(UUID id);
    List<Event> getPublicCatalog();
    List<Event> getEventsByCreator(String username);
}
