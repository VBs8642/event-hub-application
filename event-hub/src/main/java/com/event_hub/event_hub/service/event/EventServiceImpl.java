package com.event_hub.event_hub.service.event;

import com.event_hub.event_hub.exception.ResourceOwnerException;
import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.entity.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.event_hub.event_hub.repository.user.UserRepository;
import com.event_hub.event_hub.repository.event.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Event createEvent(EventCreateUpdateDto dto, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user context not found."));

        if (!dto.getEndDateTime().isAfter(dto.getStartDateTime())) {
            throw new IllegalArgumentException("Event conclusion timestamp must occur after the kickoff timestamp.");
        }

        if (dto.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event structural capacity must be greater than zero.");
        }

        Event event = Event.builder()
                .title(dto.getTitle())
                .city(dto.getCity())
                .venue(dto.getVenue())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .capacity(dto.getCapacity())
                .ticketPrice(dto.getTicketPrice())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .creator(creator) // Clean mapping directly to your User entity
                .build();

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event updateEvent(UUID eventId, EventCreateUpdateDto dto, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Requested event record not found."));

        if (!event.getCreator().getUsername().equals(username)) {
            throw new ResourceOwnerException("Unauthorized action. You are not the creator of this event.");
        }

        if (!dto.getEndDateTime().isAfter(dto.getStartDateTime())) {
            throw new IllegalArgumentException("Event conclusion timestamp must occur after the kickoff timestamp.");
        }

        event.setTitle(dto.getTitle());
        event.setCity(dto.getCity());
        event.setVenue(dto.getVenue());
        event.setDescription(dto.getDescription());
        event.setImageUrl(dto.getImageUrl());
        event.setCapacity(dto.getCapacity());
        event.setTicketPrice(dto.getTicketPrice());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Requested event record not found."));

        if (!event.getCreator().getUsername().equals(username)) {
            throw new ResourceOwnerException("Unauthorized action. You are not the creator of this event.");
        }

        eventRepository.delete(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEventDetails(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Requested event detail record not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getPublicCatalog() {
        return eventRepository.findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByCreator(String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User context not found."));

        return eventRepository.findByCreatorIdOrderByStartDateTimeDesc(creator.getId());
    }
}
