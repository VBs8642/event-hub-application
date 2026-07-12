package com.event_hub.event_hub.service.user;

import com.event_hub.event_hub.mapper.user.UserMapper;
import com.event_hub.event_hub.model.dto.user.UserRegisterRequest;
import com.event_hub.event_hub.model.entity.registration.Registration;
import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(UserRegisterRequest registrationDto) {
        if (registrationDto == null) {
            throw new IllegalArgumentException("Registration data cannot be null.");
        }
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username is already registered.");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered.");
        }
        User userEntity = UserMapper.toUserEntity(registrationDto);

        userRepository.save(userEntity);

    }



    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    @Override
    public User findById(UUID id) {
        return null;
    }
    @Override
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return user;
    }
}
