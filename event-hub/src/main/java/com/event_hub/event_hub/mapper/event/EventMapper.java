package com.event_hub.event_hub.mapper.event;

import com.event_hub.event_hub.model.dto.event.EventCreateUpdateDto;
import com.event_hub.event_hub.model.entity.event.Event;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventMapper {
    public static Event toEntity(Event dto) {
        if (dto == null) {
            return null;
        }

        return Event.builder()
                .title(dto.getTitle())
                .city(dto.getCity())
                .venue(dto.getVenue())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .capacity(dto.getCapacity())
                .ticketPrice(dto.getTicketPrice())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .build();
    }
    public static EventCreateUpdateDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        EventCreateUpdateDto dto = new EventCreateUpdateDto();
        dto.setTitle(event.getTitle());
        dto.setCity(event.getCity());
        dto.setVenue(event.getVenue());
        dto.setDescription(event.getDescription());
        dto.setImageUrl(event.getImageUrl());
        dto.setCapacity(event.getCapacity());
        dto.setTicketPrice(event.getTicketPrice());
        dto.setStartDateTime(event.getStartDateTime());
        dto.setEndDateTime(event.getEndDateTime());
        return dto;
    }
}
