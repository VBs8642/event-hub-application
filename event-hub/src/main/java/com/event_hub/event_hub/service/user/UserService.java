package com.event_hub.event_hub.service.user;

import com.event_hub.event_hub.model.entity.registration.Registration;
import com.event_hub.event_hub.model.entity.user.User;

import java.util.UUID;

public interface UserService {
    void registerUser(Registration registrationDto);
    User findByUsername(String username);
    User findById(UUID id);
}
