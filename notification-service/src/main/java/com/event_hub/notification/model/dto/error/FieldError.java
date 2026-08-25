package com.event_hub.notification.model.dto.error;

import lombok.*;

/**
 * Field-level validation error details for microservice
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FieldError {
    private String field;
    private String message;
    private String rejectedValue;

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
