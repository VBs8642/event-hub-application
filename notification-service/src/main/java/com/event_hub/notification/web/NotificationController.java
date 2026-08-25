package com.event_hub.notification.web;

import com.event_hub.notification.model.dto.BroadcastAnnouncementRequest;
import com.event_hub.notification.model.dto.BroadcastAnnouncementResponse;
import com.event_hub.notification.model.dto.UserNotificationPreferenceRequest;
import com.event_hub.notification.model.dto.UserNotificationPreferenceResponse;
import com.event_hub.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/micro/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/broadcast")
    public ResponseEntity<BroadcastAnnouncementResponse> broadcastAnnouncement(
            @Valid @RequestBody BroadcastAnnouncementRequest request) {
        log.info("📢 Broadcasting announcement for event: {}", request.getEventId());
        BroadcastAnnouncementResponse response = notificationService.broadcastAnnouncement(request);
        log.info("✅ Announcement broadcast completed. Recipients: {}", response.getRecipientCount());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/preferences")
    public ResponseEntity<UserNotificationPreferenceResponse> savePreferences(
            @Valid @RequestBody UserNotificationPreferenceRequest request) {
        log.info("💾 Saving notification preferences for user: {}", request.getUserId());
        UserNotificationPreferenceResponse response = notificationService.saveNotificationPreference(request);
        log.info("✅ Notification preferences saved successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<UserNotificationPreferenceResponse> updatePreferences(
            @PathVariable UUID userId,
            @Valid @RequestBody UserNotificationPreferenceRequest request) {
        log.info("🔄 Updating notification preferences for user: {}", userId);
        request.setUserId(userId);
        UserNotificationPreferenceResponse response = notificationService.saveNotificationPreference(request);
        log.info("✅ Notification preferences updated successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/preferences/{userId}")
    public ResponseEntity<UserNotificationPreferenceResponse> getPreferences(
            @PathVariable UUID userId) {
        log.debug("🔍 Fetching notification preferences for user: {}", userId);
        UserNotificationPreferenceResponse response = notificationService.getNotificationPreference(userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.debug("🏥 Health check endpoint called");
        return ResponseEntity.ok("Notification service is running");
    }
}
