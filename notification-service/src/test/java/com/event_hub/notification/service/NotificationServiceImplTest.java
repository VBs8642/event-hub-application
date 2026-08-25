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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationRecipientRepository recipientRepository;

    @Mock
    private UserNotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID eventId;
    private UUID userId;
    private BroadcastAnnouncementRequest broadcastRequest;
    private UserNotificationPreferenceRequest preferenceRequest;
    private Notification testNotification;
    private UserNotificationPreference testPreference;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        broadcastRequest = BroadcastAnnouncementRequest.builder()
                .eventId(eventId)
                .title("Test Announcement")
                .content("Test Content")
                .recipientUserIds(List.of(userId))
                .build();

        preferenceRequest = UserNotificationPreferenceRequest.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(false)
                .appAlertsEnabled(true)
                .pushNotificationEnabled(true)
                .build();

        testNotification = Notification.builder()
                .id(UUID.randomUUID())
                .eventId(eventId)
                .title("Test Announcement")
                .content("Test Content")
                .type(Notification.NotificationType.ANNOUNCEMENT)
                .status(Notification.NotificationStatus.SENT)
                .build();

        testPreference = UserNotificationPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(false)
                .appAlertsEnabled(true)
                .pushNotificationEnabled(true)
                .build();
    }

    @Test
    @DisplayName("Should broadcast announcement successfully")
    void testBroadcastAnnouncement_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(recipientRepository.findByNotificationIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());
        when(recipientRepository.save(any(NotificationRecipient.class)))
                .thenReturn(NotificationRecipient.builder().build());

        BroadcastAnnouncementResponse response = notificationService.broadcastAnnouncement(broadcastRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals(1, response.getRecipientCount(), "Should have 1 recipient");
        assertEquals("SENT", response.getStatus(), "Status should be SENT");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when event ID is null")
    void testBroadcastAnnouncement_NullEventId() {
        broadcastRequest.setEventId(null);

        assertThrows(ValidationException.class, () -> {
            notificationService.broadcastAnnouncement(broadcastRequest);
        }, "Should throw ValidationException for null event ID");
    }

    @Test
    @DisplayName("Should throw ValidationException when recipients list is empty")
    void testBroadcastAnnouncement_EmptyRecipients() {
        broadcastRequest.setRecipientUserIds(List.of());

        assertThrows(ValidationException.class, () -> {
            notificationService.broadcastAnnouncement(broadcastRequest);
        }, "Should throw ValidationException for empty recipients");
    }

    @Test
    @DisplayName("Should save notification preference successfully")
    void testSaveNotificationPreference_Success() {
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(UserNotificationPreference.class))).thenReturn(testPreference);

        UserNotificationPreferenceResponse response = notificationService.saveNotificationPreference(preferenceRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals(userId, response.getUserId(), "User ID should match");
        assertTrue(response.isEmailEnabled(), "Email should be enabled");
        verify(preferenceRepository, times(1)).save(any(UserNotificationPreference.class));
    }

    @Test
    @DisplayName("Should update existing notification preference")
    void testSaveNotificationPreference_Update() {
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(testPreference));
        when(preferenceRepository.save(any(UserNotificationPreference.class))).thenReturn(testPreference);

        UserNotificationPreferenceResponse response = notificationService.saveNotificationPreference(preferenceRequest);

        assertNotNull(response, "Response should not be null");
        verify(preferenceRepository, times(1)).save(any(UserNotificationPreference.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when user ID is null")
    void testSaveNotificationPreference_NullUserId() {
        preferenceRequest.setUserId(null);

        assertThrows(ValidationException.class, () -> {
            notificationService.saveNotificationPreference(preferenceRequest);
        }, "Should throw ValidationException for null user ID");
    }

    @Test
    @DisplayName("Should get notification preference successfully")
    void testGetNotificationPreference_Success() {
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(testPreference));

        UserNotificationPreferenceResponse response = notificationService.getNotificationPreference(userId);

        assertNotNull(response, "Response should not be null");
        assertEquals(userId, response.getUserId(), "User ID should match");
        assertTrue(response.isEmailEnabled(), "Email should be enabled");
        verify(preferenceRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return default preferences when not found")
    void testGetNotificationPreference_NotFound() {
        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserNotificationPreferenceResponse response = notificationService.getNotificationPreference(userId);

        assertNotNull(response, "Response should not be null");
        assertEquals(userId, response.getUserId(), "User ID should match");
        assertTrue(response.isEmailEnabled(), "Email should be enabled by default");
    }

    @Test
    @DisplayName("Should throw ValidationException for null user ID in get preference")
    void testGetNotificationPreference_NullUserId() {
        assertThrows(ValidationException.class, () -> {
            notificationService.getNotificationPreference(null);
        }, "Should throw ValidationException for null user ID");
    }
}
