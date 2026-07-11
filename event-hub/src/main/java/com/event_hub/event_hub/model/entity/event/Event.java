package com.event_hub.event_hub.model.entity.event;

import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.model.enums.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id ;
    @NotBlank
    @Column(nullable = false, length = 100)
    private String title;
    @NotBlank
    @Column(nullable = false, length = 100)
    private String city;
    @NotBlank
    @Column(nullable = false, length = 150)
    private String venue;
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @Min(1)
    @Column(nullable = false)
    private int capacity;
    @NotNull
    @DecimalMin("0.00")
    @Column(name = "ticket_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;
    @NotNull
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;
    @NotNull
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status = EventStatus.DRAFT;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    public enum EventStatus {
        DRAFT,
        PUBLISHED,
        ONGOING,
        COMPLETED,
        CANCELLED
    }


}
