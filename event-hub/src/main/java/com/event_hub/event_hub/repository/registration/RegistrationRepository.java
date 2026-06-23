package com.event_hub.event_hub.repository.registration;

import com.event_hub.event_hub.model.entity.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    List<Registration> findByAttendeeIdAndStatus(UUID attendeeId, RegistrationStatus status);
    boolean existsByEventIdAndAttendeeIdAndStatus(UUID eventId, UUID attendeeId, RegistrationStatus status);
    @Query("SELECT COALESCE(SUM(r.attendeesCount), 0) FROM Registration r " +
            "WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    int countCurrentAttendeesByEventId(@Param("eventId") UUID eventId);


    Optional<Registration> findByEventIdAndAttendeeId(UUID eventId, UUID attendeeId);
}
