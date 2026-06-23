package com.event_hub.event_hub.service.agenda;

import com.event_hub.event_hub.model.dto.agendaItem.AgendaItemDto;
import com.event_hub.event_hub.model.entity.agendaItem.AgendaItem;

import java.util.List;
import java.util.UUID;

public interface AgendaItemService {
    AgendaItem addAgendaItem(UUID eventId, AgendaItemDto dto);
    List<AgendaItem> getAgendaByEvent(UUID eventId);
    void removeAgendaItem(UUID itemId);
}
