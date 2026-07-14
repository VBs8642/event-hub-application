package com.event_hub.event_hub.service.registarion;

import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.entity.registration.Registration;
import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.model.enums.RegistrationStatus;
import com.event_hub.event_hub.repository.event.EventRepository;
import com.event_hub.event_hub.repository.registration.RegistrationRepository;
import com.event_hub.event_hub.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
                                   EventRepository eventRepository,
                                   UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Registration registerAttendee(UUID eventId, String username, int ticketsRequested) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Business Rule: Check capacity bounds
        int currentReservationsCount = registrationRepository.countTicketsByEventId(eventId);
        if (currentReservationsCount + ticketsRequested > event.getCapacity()) {
            throw new IllegalStateException("Not enough tickets available. Remaining: "
                    + (event.getCapacity() - currentReservationsCount));
        }

        // Business Rule: Prevent duplicate registration
        boolean alreadyRegistered = registrationRepository.existsByEventIdAndAttendeeIdAndStatus(
                eventId, user.getId(), RegistrationStatus.CONFIRMED);
        if (alreadyRegistered) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Registration registration = Registration.builder()
                .event(event)
                .attendee(user)
                .attendeesCount(ticketsRequested)
                .registrationDate(LocalDateTime.now())
                .build();

        return registrationRepository.save(registration);
    }

    @Override
    @Transactional
    public void cancelRegistration(UUID eventId, String username) {
        Registration registration = registrationRepository.findByEventIdAndAttendeeUsername(eventId, username)
                .orElseThrow(() -> new IllegalArgumentException("No registration record found for this event"));

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsByUser(String username) {
        return registrationRepository.findByAttendeeUsernameOrderByRegistrationDateDesc(username);
    }
}
