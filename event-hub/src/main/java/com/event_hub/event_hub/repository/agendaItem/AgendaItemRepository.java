package com.event_hub.event_hub.repository.agendaItem;

import com.event_hub.event_hub.model.entity.agendaItem.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgendaItemRepository extends JpaRepository<AgendaItem, UUID> {
    List<AgendaItem> findByEventIdOrderByDisplayOrderAscStartTimeAsc(UUID eventId);
}
