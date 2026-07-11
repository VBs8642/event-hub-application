package com.event_hub.event_hub.repository.event;


import com.event_hub.event_hub.model.entity.event.Event;
import com.event_hub.event_hub.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface EventRepository extends JpaRepository<Event, UUID>{
    List<Event> findByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime now);
    List<Event> findByCreatorIdOrderByStartDateTimeDesc(UUID creatorId);
}
