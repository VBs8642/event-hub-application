package com.event_hub.notification.web;

import com.event_hub.notification.model.dto.BroadcastAnnouncementRequest;
import com.event_hub.notification.model.dto.BroadcastAnnouncementResponse;
import com.event_hub.notification.model.dto.UserNotificationPreferenceRequest;
import com.event_hub.notification.model.dto.UserNotificationPreferenceResponse;
import com.event_hub.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/micro/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/broadcast")
    public ResponseEntity<BroadcastAnnouncementResponse> broadcastAnnouncement(
            @RequestBody BroadcastAnnouncementRequest request) {
        BroadcastAnnouncementResponse response = notificationService.broadcastAnnouncement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/preferences")
    public ResponseEntity<UserNotificationPreferenceResponse> savePreferences(
            @RequestBody UserNotificationPreferenceRequest request) {
        UserNotificationPreferenceResponse response = notificationService.saveNotificationPreference(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<UserNotificationPreferenceResponse> updatePreferences(
            @PathVariable UUID userId,
            @RequestBody UserNotificationPreferenceRequest request) {
        request.setUserId(userId);
        UserNotificationPreferenceResponse response = notificationService.saveNotificationPreference(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/preferences/{userId}")
    public ResponseEntity<UserNotificationPreferenceResponse> getPreferences(
            @PathVariable UUID userId) {
        UserNotificationPreferenceResponse response = notificationService.getNotificationPreference(userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification service is running");
    }
}
