package com.event_hub.notification.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("NotificationController Integration Tests")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should access health endpoint")
    void testHealthEndpoint_Success() throws Exception {
        mockMvc.perform(get("/api/micro/notifications/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification service is running"));
    }

    @Test
    @DisplayName("Should validate broadcast request")
    void testBroadcastEndpoint_InvalidData() throws Exception {
        mockMvc.perform(post("/api/micro/notifications/broadcast")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":null,\"title\":\"\",\"content\":\"\",\"recipientUserIds\":[]}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept valid preference update request")
    void testPreferencesEndpoint_InvalidData() throws Exception {
        mockMvc.perform(put("/api/micro/notifications/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":null,\"emailEnabled\":true}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle CORS requests")
    void testCorsHandling() throws Exception {
        mockMvc.perform(options("/api/micro/notifications/broadcast")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().is2xxSuccessful());
    }
}
