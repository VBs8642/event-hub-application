package com.event_hub.event_hub.model.dto.event;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateUpdateDto {
    @NotBlank(message = "Event title is required.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;
    @NotBlank(message = "City location is required.")
    private String city;
    @NotBlank(message = "Venue name is required.")
    private String venue;
    @NotBlank(message = "Please provide an event description.")
    private String description;
    @Pattern(regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif))?$", message = "Must be a valid image URL.")
    private String imageUrl;
    @Min(value = 1, message = "Event capacity must be at least 1 person.")
    private int capacity;
    @NotNull(message = "Ticket price is required.")
    @DecimalMin(value = "0.00", message = "Ticket price cannot be negative.")
    private BigDecimal ticketPrice;
    @NotNull(message = "Start date and time are required.")
    private LocalDateTime startDateTime;
    @NotNull(message = "End date and time are required.")
    private LocalDateTime endDateTime;


}
