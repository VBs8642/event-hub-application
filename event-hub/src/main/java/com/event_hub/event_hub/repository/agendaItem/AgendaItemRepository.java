package com.event_hub.event_hub.repository.agendaItem;

import com.event_hub.event_hub.model.entity.agendaItem.AgendaItem;

import java.util.List;
import java.util.UUID;

public interface AgendaItemRepository {
    List<AgendaItem> findByEventIdOrderByDisplayOrderAscStartTimeAsc(UUID eventId);
}
