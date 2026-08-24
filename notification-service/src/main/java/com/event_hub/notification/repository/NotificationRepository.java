package com.event_hub.notification.repository;

import com.event_hub.notification.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByEventId(UUID eventId);
    List<Notification> findByEventIdOrderByCreatedAtDesc(UUID eventId);
    Optional<Notification> findByIdAndEventId(UUID id, UUID eventId);
}
