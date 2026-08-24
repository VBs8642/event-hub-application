package com.event_hub.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastAnnouncementResponse {
    @JsonProperty("notification_id")
    private UUID notificationId;

    @JsonProperty("recipients_count")
    private int recipientsCount;

    private String message;

    @JsonProperty("status")
    private String status;
}
