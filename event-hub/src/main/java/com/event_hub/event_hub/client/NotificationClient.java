package com.event_hub.event_hub.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
    name = "notification-service",
    url = "http://localhost:8081"
)
public interface NotificationClient {

    @PostMapping("/api/micro/notifications/broadcast")
    ResponseEntity<BroadcastAnnouncementResponse> broadcastAnnouncement(
            @RequestBody BroadcastAnnouncementRequest request
    );


    @PutMapping("/api/micro/notifications/preferences/{userId}")
    ResponseEntity<UserNotificationPreferenceResponse> updateNotificationPreferences(
            @PathVariable UUID userId,
            @RequestBody UserNotificationPreferenceRequest request
    );

    @GetMapping("/api/micro/notifications/preferences/{userId}")
    ResponseEntity<UserNotificationPreferenceResponse> getNotificationPreferences(
            @PathVariable UUID userId
    );

    @GetMapping("/api/micro/notifications/health")
    ResponseEntity<String> health();
}


class BroadcastAnnouncementRequest {
    private UUID eventId;
    private String title;
    private String content;
    private java.util.List<UUID> recipientUserIds;

    public BroadcastAnnouncementRequest() {}

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public java.util.List<UUID> getRecipientUserIds() { return recipientUserIds; }
    public void setRecipientUserIds(java.util.List<UUID> recipientUserIds) { this.recipientUserIds = recipientUserIds; }
}

class BroadcastAnnouncementResponse {
    private UUID notificationId;
    private int recipientsCount;
    private String message;
    private String status;

    public BroadcastAnnouncementResponse() {}

    public UUID getNotificationId() { return notificationId; }
    public void setNotificationId(UUID notificationId) { this.notificationId = notificationId; }

    public int getRecipientsCount() { return recipientsCount; }
    public void setRecipientsCount(int recipientsCount) { this.recipientsCount = recipientsCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class UserNotificationPreferenceRequest {
    private UUID userId;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean appAlertsEnabled;
    private boolean pushNotificationEnabled;

    public UserNotificationPreferenceRequest() {}

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }

    public boolean isAppAlertsEnabled() { return appAlertsEnabled; }
    public void setAppAlertsEnabled(boolean appAlertsEnabled) { this.appAlertsEnabled = appAlertsEnabled; }

    public boolean isPushNotificationEnabled() { return pushNotificationEnabled; }
    public void setPushNotificationEnabled(boolean pushNotificationEnabled) { this.pushNotificationEnabled = pushNotificationEnabled; }
}

class UserNotificationPreferenceResponse {
    private UUID preferenceId;
    private UUID userId;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean appAlertsEnabled;
    private boolean pushNotificationEnabled;
    private java.time.LocalDateTime updatedAt;

    public UserNotificationPreferenceResponse() {}

    public UUID getPreferenceId() { return preferenceId; }
    public void setPreferenceId(UUID preferenceId) { this.preferenceId = preferenceId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }

    public boolean isAppAlertsEnabled() { return appAlertsEnabled; }
    public void setAppAlertsEnabled(boolean appAlertsEnabled) { this.appAlertsEnabled = appAlertsEnabled; }

    public boolean isPushNotificationEnabled() { return pushNotificationEnabled; }
    public void setPushNotificationEnabled(boolean pushNotificationEnabled) { this.pushNotificationEnabled = pushNotificationEnabled; }

    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
