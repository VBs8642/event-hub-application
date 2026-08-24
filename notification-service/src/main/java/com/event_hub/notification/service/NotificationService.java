package com.event_hub.notification.service;

import com.event_hub.notification.model.dto.BroadcastAnnouncementRequest;
import com.event_hub.notification.model.dto.BroadcastAnnouncementResponse;
import com.event_hub.notification.model.dto.UserNotificationPreferenceRequest;
import com.event_hub.notification.model.dto.UserNotificationPreferenceResponse;

import java.util.UUID;

public interface NotificationService {
    BroadcastAnnouncementResponse broadcastAnnouncement(BroadcastAnnouncementRequest request);
    UserNotificationPreferenceResponse saveNotificationPreference(UserNotificationPreferenceRequest request);
    UserNotificationPreferenceResponse getNotificationPreference(UUID userId);
}
