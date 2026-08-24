package com.event_hub.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationPreferenceResponse {
    @JsonProperty("preference_id")
    private UUID preferenceId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("email_enabled")
    private boolean emailEnabled;

    @JsonProperty("sms_enabled")
    private boolean smsEnabled;

    @JsonProperty("app_alerts_enabled")
    private boolean appAlertsEnabled;

    @JsonProperty("push_notification_enabled")
    private boolean pushNotificationEnabled;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
