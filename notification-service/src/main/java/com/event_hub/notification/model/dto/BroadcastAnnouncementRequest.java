package com.event_hub.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastAnnouncementRequest {
    @JsonProperty("event_id")
    @NotNull(message = "Event ID is required.")
    private UUID eventId;

    @NotBlank(message = "Announcement title is required.")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters.")
    private String title;

    @NotBlank(message = "Announcement content is required.")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters.")
    private String content;
    
    @JsonProperty("recipient_user_ids")
    @NotEmpty(message = "At least one recipient is required.")
    private List<UUID> recipientUserIds;
}
