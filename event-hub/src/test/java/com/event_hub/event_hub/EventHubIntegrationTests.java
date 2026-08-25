package com.event_hub.event_hub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventHubIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testApplicationContextLoads() {
        assertNotNull(restTemplate, "TestRestTemplate should be injected");
    }

    @Test
    public void testPublicCatalogEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/events/catalog", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Public catalog should be accessible");
        assertNotNull(response.getBody(), "Response body should not be null");
    }

    @Test
    public void testRegisterEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/register", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Register page should be accessible");
    }

    @Test
    public void testLoginEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Login page should be accessible");
    }
}
