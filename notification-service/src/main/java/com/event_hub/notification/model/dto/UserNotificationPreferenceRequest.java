package com.event_hub.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationPreferenceRequest {
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
}
