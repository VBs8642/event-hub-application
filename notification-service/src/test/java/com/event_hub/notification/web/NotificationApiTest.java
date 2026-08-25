package com.event_hub.notification.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Notification API Tests")
class NotificationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 200 for health check")
    void testHealthCheckEndpoint() throws Exception {
        mockMvc.perform(get("/api/micro/notifications/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should accept POST to broadcast endpoint")
    void testBroadcastEndpointAcceptsPost() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String requestBody = String.format("""
                {
                    "event_id": "%s",
                    "title": "Test Announcement",
                    "content": "Test Content",
                    "recipient_user_ids": ["%s"]
                }
                """, eventId, userId);

        mockMvc.perform(post("/api/micro/notifications/broadcast")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept PUT to preferences endpoint")
    void testPreferencesEndpointAcceptsPut() throws Exception {
        UUID userId = UUID.randomUUID();
        String requestBody = String.format("""
                {
                    "user_id": "%s",
                    "email_enabled": true,
                    "sms_enabled": false,
                    "app_alerts_enabled": true,
                    "push_notification_enabled": true
                }
                """, userId);

        mockMvc.perform(put("/api/micro/notifications/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should accept GET to preferences with user ID")
    void testGetPreferencesEndpoint() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/micro/notifications/preferences/" + userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should reject invalid broadcast request")
    void testBroadcastEndpointValidation() throws Exception {
        String invalidRequest = "{\"eventId\":null,\"title\":\"\"}";

        mockMvc.perform(post("/api/micro/notifications/broadcast")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should support CORS for cross-origin requests")
    void testCorsSupport() throws Exception {
        mockMvc.perform(options("/api/micro/notifications/broadcast")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().is2xxSuccessful());
    }
}
