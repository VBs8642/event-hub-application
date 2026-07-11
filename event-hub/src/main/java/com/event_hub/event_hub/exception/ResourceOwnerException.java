package com.event_hub.event_hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You do not own this resource")
public class ResourceOwnerException extends RuntimeException{
    public ResourceOwnerException(String message) {
        super(message);
    }
}
