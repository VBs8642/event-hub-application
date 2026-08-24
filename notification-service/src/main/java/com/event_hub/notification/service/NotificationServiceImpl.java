package com.event_hub.notification.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;

    @Override
    public BroadcastAnnouncementResponse broadcastAnnouncement(BroadcastAnnouncementRequest request) {

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


        if (request.getRecipientUserIds() != null && !request.getRecipientUserIds().isEmpty()) {
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
                }
            }
        }

        return BroadcastAnnouncementResponse.builder()
                .notificationId(savedNotification.getId())
                .recipientsCount(request.getRecipientUserIds() != null ? request.getRecipientUserIds().size() : 0)
                .message("Announcement sent successfully")
                .status("SENT")
                .build();
    }

    @Override
    public UserNotificationPreferenceResponse saveNotificationPreference(UserNotificationPreferenceRequest request) {
        Optional<UserNotificationPreference> existing = preferenceRepository.findByUserId(request.getUserId());

        UserNotificationPreference preference;
        if (existing.isPresent()) {
            preference = existing.get();
            preference.setEmailEnabled(request.isEmailEnabled());
            preference.setSmsEnabled(request.isSmsEnabled());
            preference.setAppAlertsEnabled(request.isAppAlertsEnabled());
            preference.setPushNotificationEnabled(request.isPushNotificationEnabled());
            preference.setUpdatedAt(LocalDateTime.now());
        } else {
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
        Optional<UserNotificationPreference> preference = preferenceRepository.findByUserId(userId);

        if (preference.isEmpty()) {

            UserNotificationPreference defaults = UserNotificationPreference.builder()
                    .userId(userId)
                    .emailEnabled(true)
                    .smsEnabled(false)
                    .appAlertsEnabled(true)
                    .pushNotificationEnabled(true)
                    .build();
            return mapToResponse(defaults);
        }

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
