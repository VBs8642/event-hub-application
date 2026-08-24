package com.event_hub.event_hub.service.event;

import com.event_hub.event_hub.exception.ResourceOwnerException;
import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.event_hub.event_hub.repository.user.UserRepository;
import com.event_hub.event_hub.repository.event.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"events", "publicCatalog"}, allEntries = true)
    public Event createEvent(EventCreateUpdateDto dto, String username) {
        log.info("📝 Creating new event: '{}'", dto.getTitle());
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user context not found."));

        if (!dto.getEndDateTime().isAfter(dto.getStartDateTime())) {
            log.warn("⚠️  Event creation failed: end time before start time");
            throw new IllegalArgumentException("Event conclusion timestamp must occur after the kickoff timestamp.");
        }

        if (dto.getCapacity() <= 0) {
            log.warn("⚠️  Event creation failed: invalid capacity");
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
                .creator(creator)
                .build();

        Event saved = eventRepository.save(event);
        log.info("✅ Event created successfully: '{}' (ID: {})", saved.getTitle(), saved.getId());
        return saved;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"events", "eventDetails", "publicCatalog"}, allEntries = true)
    public Event updateEvent(UUID eventId, EventCreateUpdateDto dto, String requestingUsername) {
        log.info("📝 Updating event: ID {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Requested event record not found."));

        User requester = userRepository.findByUsername(requestingUsername)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user context not found."));

        boolean isAdmin = requester.getRole() == UserRole.ADMIN;
        if (!isAdmin && !event.getCreator().getUsername().equals(requestingUsername)) {
            log.warn("⚠️  Unauthorized event update attempt by user: {}", requestingUsername);
            throw new ResourceOwnerException("Unauthorized action. You are not the creator of this event.");
        }

        if (!dto.getEndDateTime().isAfter(dto.getStartDateTime())) {
            log.warn("⚠️  Event update failed: end time before start time");
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

        Event updated = eventRepository.save(event);
        log.info("✅ Event updated successfully: '{}' (ID: {})", updated.getTitle(), updated.getId());
        return updated;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"events", "eventDetails", "publicCatalog", "userEvents"}, allEntries = true)
    public void deleteEvent(UUID eventId, String requestingUsername) {
        log.info("🗑️  Deleting event: ID {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Requested event record not found."));

        User requester = userRepository.findByUsername(requestingUsername)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user context not found."));

        boolean isAdmin = requester.getRole() == UserRole.ADMIN;
        if (!isAdmin && !event.getCreator().getUsername().equals(requestingUsername)) {
            log.warn("⚠️  Unauthorized event delete attempt by user: {}", requestingUsername);
            throw new ResourceOwnerException("Unauthorized action. You are not the creator of this event.");
        }

        eventRepository.delete(event);
        log.info("✅ Event deleted successfully: '{}' (ID: {})", event.getTitle(), event.getId());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "eventDetails", key = "#id", unless = "#result == null")
    public Event getEventDetails(UUID id) {
        log.debug("🔍 Fetching event details: ID {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Requested event detail record not found."));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "publicCatalog", unless = "#result == null or #result.isEmpty()")
    public List<Event> getPublicCatalog() {
        log.info("📋 Fetching public event catalog");
        List<Event> events = eventRepository.findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime.now());
        log.debug("Found {} upcoming events in catalog", events.size());
        return events;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userEvents", key = "#username", unless = "#result == null or #result.isEmpty()")
    public List<Event> getEventsByCreator(String username) {
        log.info("📋 Fetching events created by user: {}", username);
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User context not found."));
        List<Event> events = eventRepository.findByCreatorIdOrderByStartDateTimeDesc(creator.getId());
        log.debug("Found {} events created by {}", events.size(), username);
        return events;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", unless = "#result == null or #result.isEmpty()")
    public List<Event> getAllEvents() {
        log.info("📋 Fetching all events (admin view)");
        List<Event> events = eventRepository.findAll();
        log.debug("Found {} total events", events.size());
        return events;
    }
}
