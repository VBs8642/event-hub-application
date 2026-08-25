package com.event_hub.event_hub.service.event;

import com.event_hub.event_hub.exception.ResourceNotFoundException;
import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.repository.event.EventRepository;
import com.event_hub.event_hub.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private User eventCreator;
    private Event testEvent;
    private EventCreateUpdateDto eventDto;
    private UUID eventId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        eventCreator = User.builder()
                .id(userId)
                .username("organizer")
                .email("organizer@test.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.ORGANIZER)
                .active(true)
                .build();

        testEvent = Event.builder()
                .id(eventId)
                .title("Test Event")
                .description("Test Description")
                .city("Test City")
                .venue("Test Venue")
                .imageUrl("https://test.com/image.jpg")
                .capacity(100)
                .ticketPrice(new BigDecimal("50.00"))
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .creator(eventCreator)
                .active(true)
                .build();

        eventDto = EventCreateUpdateDto.builder()
                .title("Updated Event")
                .description("Updated Description")
                .city("Updated City")
                .venue("Updated Venue")
                .imageUrl("https://test.com/updated.jpg")
                .capacity(150)
                .ticketPrice(new BigDecimal("75.00"))
                .startDateTime(LocalDateTime.now().plusDays(2))
                .endDateTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .build();
    }

    @Test
    @DisplayName("Should get event details successfully")
    void testGetEventDetails_Success() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        Event result = eventService.getEventDetails(eventId);

        assertNotNull(result, "Event should not be null");
        assertEquals("Test Event", result.getTitle(), "Event title should match");
        assertEquals(eventId, result.getId(), "Event ID should match");
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when event not found")
    void testGetEventDetails_NotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventDetails(eventId);
        }, "Should throw ResourceNotFoundException");

        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    @DisplayName("Should get all events successfully")
    void testGetAllEvents_Success() {
        List<Event> events = List.of(testEvent);
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertNotNull(result, "Events list should not be null");
        assertEquals(1, result.size(), "Should return 1 event");
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get public catalog successfully")
    void testGetPublicCatalog_Success() {
        List<Event> events = List.of(testEvent);
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getPublicCatalog();

        assertNotNull(result, "Public catalog should not be null");
        assertEquals(1, result.size(), "Should return 1 event");
    }

    @Test
    @DisplayName("Should get events by creator successfully")
    void testGetEventsByCreator_Success() {
        List<Event> events = List.of(testEvent);
        when(eventRepository.findByCreatorUsername("organizer")).thenReturn(events);

        List<Event> result = eventService.getEventsByCreator("organizer");

        assertNotNull(result, "Events should not be null");
        assertEquals(1, result.size(), "Should return 1 event");
        verify(eventRepository, times(1)).findByCreatorUsername("organizer");
    }

    @Test
    @DisplayName("Should return empty list when creator has no events")
    void testGetEventsByCreator_EmptyList() {
        when(eventRepository.findByCreatorUsername("organizer")).thenReturn(List.of());

        List<Event> result = eventService.getEventsByCreator("organizer");

        assertNotNull(result, "Events should not be null");
        assertEquals(0, result.size(), "Should return empty list");
    }

    @Test
    @DisplayName("Should create event successfully")
    void testCreateEvent_Success() {
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(eventCreator));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        eventService.createEvent(eventDto, "organizer");

        verify(userRepository, times(1)).findByUsername("organizer");
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should update event successfully")
    void testUpdateEvent_Success() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(eventCreator));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        eventService.updateEvent(eventId, eventDto, "organizer");

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should delete event successfully")
    void testDeleteEvent_Success() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(userRepository.findByUsername("organizer")).thenReturn(Optional.of(eventCreator));

        eventService.deleteEvent(eventId, "organizer");

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).delete(testEvent);
    }
}
