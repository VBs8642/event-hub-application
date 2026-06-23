package com.event_hub.event_hub.repository.event;


import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.enums.EventStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface EventRepository {
    List<Event> findByStatusAndStartDateTimeAfterOrderByStartDateTimeAsc(EventStatus status, LocalDateTime now);
    List<Event> findByCreatorIdOrderByStartDateTimeDesc(UUID creatorId);
}
