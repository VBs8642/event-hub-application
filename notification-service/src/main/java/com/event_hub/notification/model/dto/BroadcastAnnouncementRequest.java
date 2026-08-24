package com.event_hub.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private UUID eventId;

    private String title;
    private String content;
    
    @JsonProperty("recipient_user_ids")
    private List<UUID> recipientUserIds;
}
