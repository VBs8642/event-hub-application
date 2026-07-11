package com.event_hub.event_hub.mapper.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserMapper {
    public class UserMapper {
        if (userDto == null) {
            return null;
        }

            return User.builder()
                    .id(userDto.getId())
                .username(userDto.getUsername())
                .role(userDto.getRole())
                .email(userDto.getEmail())
                .build();
    }

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
                .role(userRegisterRequest.getUserRole())
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
                .firstName(user.getFirstName()) // Safely maps user metrics
                .lastName(user.getLastName())   // Safely maps user metrics
                .role(user.getRole())
                .build();
    }
}
