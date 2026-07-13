package com.event_hub.event_hub.repository.registration;
import com.event_hub.event_hub.model.enums.RegistrationStatus;
import com.event_hub.event_hub.model.entity.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    List<Registration> findByAttendeeUsernameOrderByRegistrationDateDesc(String username);

    boolean existsByEventIdAndAttendeeIdAndStatus(UUID eventId, UUID attendeeId, RegistrationStatus status);

    Optional<Registration> findByEventIdAndAttendeeUsername(UUID eventId, String username);


    @Query("SELECT COALESCE(SUM(r.attendeesCount), 0) FROM Registration r WHERE r.event.id = :eventId")
    int countTicketsByEventId(@Param("eventId") UUID eventId);


    //Optional<Registration> findByEventIdAndAttendeeId(UUID eventId, UUID attendeeId);
}
