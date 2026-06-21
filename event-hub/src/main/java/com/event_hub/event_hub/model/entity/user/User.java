package com.event_hub.event_hub.model.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank @Column(nullable = false)
    private String password;

    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String profilePicture;

    @Enumerated(EnumType.STRING)
   // private Role role = Role.USER;

    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();


}
