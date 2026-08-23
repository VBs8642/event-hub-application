package com.event_hub.event_hub.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // Admin-only: user management
                .requestMatchers("/users/**").hasRole("ADMIN")

                // Event creator (ORGANIZER) + Admin: manage events, agenda, and dashboard
                // These specific paths must appear BEFORE the wildcard GET /events/* below
                .requestMatchers("/events/create", "/events/edit/**",
                        "/events/delete/**", "/events/dashboard").hasAnyRole("ORGANIZER", "ADMIN")
                .requestMatchers("/agenda/**").hasAnyRole("ORGANIZER", "ADMIN")

                // Authenticated users: register for events and manage their own data
                .requestMatchers("/registrations/**", "/profile/**").authenticated()

                // Public pages (guests)
                .requestMatchers("/", "/login", "/register", "/error").permitAll()
                // Public event browsing: catalog and individual detail pages (GET only)
                .requestMatchers(HttpMethod.GET, "/events/catalog", "/events/*").permitAll()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/events/catalog", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
