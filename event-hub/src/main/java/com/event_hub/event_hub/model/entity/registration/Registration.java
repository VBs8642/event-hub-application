package com.event_hub.event_hub.model.entity.registration;

import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.model.enums.RegistrationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;

    @Column(name = "registration_date", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Min(1)
    @Column(name = "attendees_count", nullable = false)
    private int attendeesCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.CONFIRMED;
}
