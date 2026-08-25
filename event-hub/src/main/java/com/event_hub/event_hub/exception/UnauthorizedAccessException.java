package com.event_hub.event_hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
