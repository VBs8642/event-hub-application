package com.event_hub.event_hub.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
}
