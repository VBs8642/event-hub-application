package com.event_hub.event_hub.model.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementRequest {
    @NotBlank(message = "Announcement title is required.")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters.")
    private String title;

    @NotBlank(message = "Announcement content is required.")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters.")
    private String content;
}
