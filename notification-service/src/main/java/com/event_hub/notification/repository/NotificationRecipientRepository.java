package com.event_hub.notification.repository;

import com.event_hub.notification.model.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, UUID> {
    List<NotificationRecipient> findByNotificationId(UUID notificationId);
    List<NotificationRecipient> findByUserId(UUID userId);
    Optional<NotificationRecipient> findByNotificationIdAndUserId(UUID notificationId, UUID userId);
}
