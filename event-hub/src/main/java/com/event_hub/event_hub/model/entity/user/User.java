package com.event_hub.event_hub.model.entity.user;

import com.event_hub.event_hub.model.dto.user.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    private UserRole role = UserRole.USER;

    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();


}
