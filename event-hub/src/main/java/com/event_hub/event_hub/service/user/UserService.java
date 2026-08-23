package com.event_hub.event_hub.service.user;

import com.event_hub.event_hub.model.dto.user.UserRegisterRequest;
import com.event_hub.event_hub.model.dto.user.UserRole;
import com.event_hub.event_hub.model.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {
    void registerUser(UserRegisterRequest registrationDto);
    User findByUsername(String username);
    User findById(UUID id);
    User updateUser(User user);
    List<User> getAllUsers();
    void changeUserRole(UUID userId, UserRole newRole);
    void toggleUserStatus(UUID userId);
}
