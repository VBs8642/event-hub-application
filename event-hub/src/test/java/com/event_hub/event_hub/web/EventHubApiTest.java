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


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("EventHub API Tests")
class EventHubApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 200 OK for public catalog")
    void testApiPublicCatalogEndpoint() throws Exception {
        mockMvc.perform(get("/events/catalog")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 200 OK for login page")
    void testApiLoginEndpoint() throws Exception {
        mockMvc.perform(get("/login")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 200 OK for register page")
    void testApiRegisterEndpoint() throws Exception {
        mockMvc.perform(get("/register")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject POST to register with invalid data")
    void testApiRegisterInvalidData() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "ab")
                .param("email", "invalid-email")
                .param("password", "123")
                .param("firstName", "")
                .param("lastName", "Test"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should handle OPTIONS request")
    void testCorsOptions() throws Exception {
        mockMvc.perform(options("/events/catalog"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Should redirect index to catalog")
    void testIndexRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }
}
