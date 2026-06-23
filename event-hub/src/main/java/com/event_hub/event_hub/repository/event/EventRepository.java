package com.event_hub.event_hub.repository.event;


import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface EventRepository {
    List<Event> findByStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(EventStatus status, LocalDateTime now);
    List<Event> findByCreatorIdOrderByStartDateTimeDesc(UUID creatorId);
}
