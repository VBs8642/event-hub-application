package com.event_hub.event_hub.service.registarion;

import com.event_hub.event_hub.model.entity.registration.Registration;

import java.util.List;
import java.util.UUID;

public interface RegistrationService {
    Registration registerAttendee(UUID eventId, String username, int ticketsRequested);
    void cancelRegistration(UUID eventId, String username);
    List<Registration> getRegistrationsByUser(String username);
}
