package com.event_hub.event_hub.model.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String error;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    private List<FieldError> fieldErrors;

    public ErrorResponse(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message, String error, String path) {
        this(status, message, error);
        this.path = path;
    }
}
