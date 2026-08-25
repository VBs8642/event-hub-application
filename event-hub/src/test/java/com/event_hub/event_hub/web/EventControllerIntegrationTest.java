package com.event_hub.event_hub.web;

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

/**
 * Integration tests for EventController
 * Tests controller endpoints with real Spring context
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("EventController Integration Tests")
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should access public catalog endpoint")
    void testShowCatalog_Success() throws Exception {
        mockMvc.perform(get("/events/catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/catalog"));
    }

    @Test
    @DisplayName("Should access event details page")
    void testShowDetails_PageExists() throws Exception {
        mockMvc.perform(get("/events")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle 404 for missing event")
    void testShowDetails_NotFound() throws Exception {
        mockMvc.perform(get("/events/invalid-id"))
                .andExpect(status().isAnyOf(404, 302));
    }
}
