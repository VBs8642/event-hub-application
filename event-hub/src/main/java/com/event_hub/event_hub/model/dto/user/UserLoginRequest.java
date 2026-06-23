package com.event_hub.event_hub.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserLoginRequest {

    @Email(message = "Email should be valid")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
