package com.event_hub.event_hub.schedule;

import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.enums.EventStatus;
import com.event_hub.event_hub.repository.event.EventRepository;
import com.event_hub.event_hub.service.event.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventScheduledTasks {

    private final EventRepository eventRepository;
    private final EventService eventService;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    @CacheEvict(value = {"events", "publicCatalog", "eventDetails", "adminDashboard"}, allEntries = true)
    public void cleanupExpiredEvents() {
        log.info("🔄 [SCHEDULED TASK] Starting cleanup of expired events...");
        LocalDateTime now = LocalDateTime.now();
        
        try {

            List<Event> expiredEvents = eventRepository.findAll().stream()
                    .filter(event -> event.getEndDateTime().isBefore(now) && 
                            event.getStatus() != EventStatus.CANCELLED &&
                            event.getStatus() != EventStatus.COMPLETED)
                    .toList();

            if (!expiredEvents.isEmpty()) {
                log.info("📅 Found {} expired events to update", expiredEvents.size());
                
                expiredEvents.forEach(event -> {
                    event.setStatus(EventStatus.COMPLETED);
                    eventRepository.save(event);
                    log.debug("✓ Marked event '{}' (ID: {}) as COMPLETED", event.getTitle(), event.getId());
                });
                
                log.info("✅ Cleanup completed successfully. {} events marked as completed", expiredEvents.size());
            } else {
                log.info("ℹ️  No expired events found");
            }
        } catch (Exception e) {
            log.error("❌ Error during event cleanup task", e);
        }
    }


    @Scheduled(fixedDelay = 300000, initialDelay = 60000)
    @Transactional
    @CacheEvict(value = {"events", "eventDetails", "publicCatalog"}, allEntries = true)
    public void updateEventStatuses() {
        log.info("🔄 [TRIGGER-BASED TASK] Starting event status update check...");
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = 0;

        try {
            List<Event> allEvents = eventRepository.findAll();
            log.debug("Checking status for {} events", allEvents.size());

            for (Event event : allEvents) {
                EventStatus oldStatus = event.getStatus();
                

                if (event.getEndDateTime().isBefore(now) && event.getStatus() != EventStatus.COMPLETED) {
                    event.setStatus(EventStatus.COMPLETED);
                    updatedCount++;
                } else if (event.getStartDateTime().isBefore(now) && 
                          event.getEndDateTime().isAfter(now) && 
                          event.getStatus() != EventStatus.ONGOING) {
                    event.setStatus(EventStatus.ONGOING);
                    updatedCount++;
                } else if (event.getStartDateTime().isAfter(now) && event.getStatus() == EventStatus.DRAFT) {
                    event.setStatus(EventStatus.PUBLISHED);
                    updatedCount++;
                }
                
                if (!oldStatus.equals(event.getStatus())) {
                    eventRepository.save(event);
                    log.debug("📊 Event '{}' status updated: {} → {}", 
                            event.getTitle(), oldStatus, event.getStatus());
                }
            }

            if (updatedCount > 0) {
                log.info("✅ Event status update completed: {} events updated", updatedCount);
            } else {
                log.debug("ℹ️  No event status changes needed");
            }
        } catch (Exception e) {
            log.error("❌ Error during event status update task", e);
        }
    }


    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    @CacheEvict(value = {"events", "eventDetails"}, allEntries = true)
    public void cleanupOldDraftEvents() {
        log.info("🔄 [SCHEDULED TASK] Cleaning up old DRAFT events (older than 30 days)...");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        try {
            List<Event> oldDrafts = eventRepository.findAll().stream()
                    .filter(event -> event.getStatus() == EventStatus.DRAFT && 
                            event.getCreatedAt() != null &&
                            event.getCreatedAt().isBefore(thirtyDaysAgo))
                    .toList();

            if (!oldDrafts.isEmpty()) {
                log.info("🗑️  Found {} old DRAFT events to delete", oldDrafts.size());
                oldDrafts.forEach(event -> {
                    eventRepository.delete(event);
                    log.debug("🗑️  Deleted old draft event: {} (ID: {})", event.getTitle(), event.getId());
                });
                log.info("✅ Old DRAFT events cleanup completed. Deleted {} events", oldDrafts.size());
            } else {
                log.info("ℹ️  No old DRAFT events found");
            }
        } catch (Exception e) {
            log.error("❌ Error during old draft events cleanup", e);
        }
    }
}
