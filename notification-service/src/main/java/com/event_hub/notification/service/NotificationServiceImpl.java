package com.event_hub.notification.service;

import com.event_hub.notification.exception.ValidationException;
import com.event_hub.notification.model.dto.BroadcastAnnouncementRequest;
import com.event_hub.notification.model.dto.BroadcastAnnouncementResponse;
import com.event_hub.notification.model.dto.UserNotificationPreferenceRequest;
import com.event_hub.notification.model.dto.UserNotificationPreferenceResponse;
import com.event_hub.notification.model.entity.Notification;
import com.event_hub.notification.model.entity.NotificationRecipient;
import com.event_hub.notification.model.entity.UserNotificationPreference;
import com.event_hub.notification.repository.NotificationRecipientRepository;
import com.event_hub.notification.repository.NotificationRepository;
import com.event_hub.notification.repository.UserNotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;

    @Override
    public BroadcastAnnouncementResponse broadcastAnnouncement(BroadcastAnnouncementRequest request) {
        log.info("📢 Starting broadcast announcement process for event: {}", request.getEventId());
        
        // Validate request
        if (request.getEventId() == null) {
            log.warn("❌ Event ID is null in broadcast request");
            throw new ValidationException("Event ID is required");
        }
        if (request.getRecipientUserIds() == null || request.getRecipientUserIds().isEmpty()) {
            log.warn("⚠️ No recipients provided for event: {}", request.getEventId());
            throw new ValidationException("At least one recipient is required");
        }
        
        log.debug("📋 Creating notification entity for event: {} with {} recipients", 
            request.getEventId(), request.getRecipientUserIds().size());

        Notification notification = Notification.builder()
                .eventId(request.getEventId())
                .title(request.getTitle())
                .content(request.getContent())
                .type(Notification.NotificationType.ANNOUNCEMENT)
                .status(Notification.NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.debug("✅ Notification entity saved with ID: {}", savedNotification.getId());

        // Create recipients
        if (request.getRecipientUserIds() != null && !request.getRecipientUserIds().isEmpty()) {
            int recipientCount = 0;
            for (UUID userId : request.getRecipientUserIds()) {
                if (recipientRepository.findByNotificationIdAndUserId(savedNotification.getId(), userId).isEmpty()) {
                    NotificationRecipient recipient = NotificationRecipient.builder()
                            .notification(savedNotification)
                            .userId(userId)
                            .deliveryStatus(NotificationRecipient.DeliveryStatus.SENT)
                            .deliveredAt(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build();
                    recipientRepository.save(recipient);
                    recipientCount++;
                }
            }
            log.info("✅ Created {} notification recipients for event: {}", recipientCount, request.getEventId());
        }

        BroadcastAnnouncementResponse response = BroadcastAnnouncementResponse.builder()
                .notificationId(savedNotification.getId())
                .recipientsCount(request.getRecipientUserIds() != null ? request.getRecipientUserIds().size() : 0)
                .message("Announcement sent successfully")
                .status("SENT")
                .build();
        
        log.info("✅ Broadcast announcement completed successfully");
        return response;
    }

    @Override
    public UserNotificationPreferenceResponse saveNotificationPreference(UserNotificationPreferenceRequest request) {
        log.info("💾 Saving notification preference for user: {}", request.getUserId());
        
        // Validate request
        if (request.getUserId() == null) {
            log.warn("❌ User ID is null in preference request");
            throw new ValidationException("User ID is required");
        }
        
        Optional<UserNotificationPreference> existing = preferenceRepository.findByUserId(request.getUserId());

        UserNotificationPreference preference;
        if (existing.isPresent()) {
            log.debug("🔄 Updating existing notification preferences for user: {}", request.getUserId());
            preference = existing.get();
            preference.setEmailEnabled(request.isEmailEnabled());
            preference.setSmsEnabled(request.isSmsEnabled());
            preference.setAppAlertsEnabled(request.isAppAlertsEnabled());
            preference.setPushNotificationEnabled(request.isPushNotificationEnabled());
            preference.setUpdatedAt(LocalDateTime.now());
        } else {
            log.debug("➕ Creating new notification preferences for user: {}", request.getUserId());
            preference = UserNotificationPreference.builder()
                    .userId(request.getUserId())
                    .emailEnabled(request.isEmailEnabled())
                    .smsEnabled(request.isSmsEnabled())
                    .appAlertsEnabled(request.isAppAlertsEnabled())
                    .pushNotificationEnabled(request.isPushNotificationEnabled())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        UserNotificationPreference saved = preferenceRepository.save(preference);
        log.info("✅ Notification preference saved for user: {}", saved.getUserId());

        return UserNotificationPreferenceResponse.builder()
                .preferenceId(saved.getId())
                .userId(saved.getUserId())
                .emailEnabled(saved.isEmailEnabled())
                .smsEnabled(saved.isSmsEnabled())
                .appAlertsEnabled(saved.isAppAlertsEnabled())
                .pushNotificationEnabled(saved.isPushNotificationEnabled())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserNotificationPreferenceResponse getNotificationPreference(UUID userId) {
        log.debug("🔍 Retrieving notification preference for user: {}", userId);
        
        if (userId == null) {
            log.warn("❌ User ID is null in get preference request");
            throw new ValidationException("User ID is required");
        }
        
        Optional<UserNotificationPreference> preference = preferenceRepository.findByUserId(userId);

        if (preference.isEmpty()) {
            log.debug("⚙️ No preferences found for user: {}, returning defaults", userId);

            UserNotificationPreference defaults = UserNotificationPreference.builder()
                    .userId(userId)
                    .emailEnabled(true)
                    .smsEnabled(false)
                    .appAlertsEnabled(true)
                    .pushNotificationEnabled(true)
                    .build();
            return mapToResponse(defaults);
        }

        log.debug("✅ Retrieved preferences for user: {}", userId);
        return mapToResponse(preference.get());
    }

    private UserNotificationPreferenceResponse mapToResponse(UserNotificationPreference preference) {
        return UserNotificationPreferenceResponse.builder()
                .preferenceId(preference.getId())
                .userId(preference.getUserId())
                .emailEnabled(preference.isEmailEnabled())
                .smsEnabled(preference.isSmsEnabled())
                .appAlertsEnabled(preference.isAppAlertsEnabled())
                .pushNotificationEnabled(preference.isPushNotificationEnabled())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}
