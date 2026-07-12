package com.event_hub.event_hub.model.dto.registration;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class RegistrationDto {
//    @NotBlank(message = "Username is required.")
//    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
//    private String username;
//
//    @NotBlank(message = "Email is required.")
//    @Email(message = "Please provide a valid email address.")
//    private String email;
//
//    @NotBlank(message = "Password is required.")
//    @Size(min = 6, message = "Password must be at least 6 characters long.")
//    private String password;
//
//    @NotBlank(message = "First name is required.")
//    private String firstName;
//
//    @NotBlank(message = "Last name is required.")
//    private String lastName;

      @NotNull(message = "Target event parameter identifier context context context is required.")
      private UUID eventId;

      @Min(value = 1, message = "You must reserve at least 1 seat pass.")
      @Max(value = 5, message = "You cannot exceed buying 5 tickets per transaction pass line.")
      private int attendeesCount;

      private LocalDateTime registrationDate;

      private String status;

}
