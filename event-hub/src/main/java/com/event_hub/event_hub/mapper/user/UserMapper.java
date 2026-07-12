package com.event_hub.event_hub.mapper.user;

import com.event_hub.event_hub.model.dto.user.UserDto;
import com.event_hub.event_hub.model.dto.user.UserRegisterRequest;
import com.event_hub.event_hub.model.entity.user.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserMapper {

    public static User toUserEntity(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .email(userRegisterRequest.getEmail())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
