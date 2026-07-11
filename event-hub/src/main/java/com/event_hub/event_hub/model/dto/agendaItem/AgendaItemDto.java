package com.event_hub.event_hub.model.dto.agendaItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class AgendaItemDto {
    @NotBlank(message = "Session workshop block title is required.")
    private String title;

    @NotBlank(message = "Speaker panel presentation leader name is required.")
    private String speaker;

    private String description;

    @NotNull(message = "Start tracking time baseline is required.")
    private LocalDateTime startTime;

    @NotNull(message = "Conclusion deadline target threshold time is required.")
    private LocalDateTime endTime;

    @Min(value = 1, message = "Sequence display sort ordering number index must equal 1 or higher.")
    private int displayOrder;

}
