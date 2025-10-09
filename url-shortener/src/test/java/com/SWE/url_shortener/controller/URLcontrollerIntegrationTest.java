package com.SWE.url_shortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.SWE.url_shortener.repository.UrlRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class URLcontrollerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testShortenUrlInsertsIntoDatabase() throws Exception {
        // Given: A URL to shorten
        String originalUrl = "https://example.com";
        urlrecord request = new urlrecord(originalUrl);

        // When: We call the shorten endpoint
        String response = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then: The URL should be inserted into the database
        assertTrue(urlRepository.findByOriginalUrl(originalUrl).isPresent(), 
                   "URL should be saved in the database");
        
        // And: The response should contain a short URL
        assertTrue(response.startsWith("Short URL: http://localhost:8080/"), 
                   "Response should contain the short URL");
        
        // And: The short code should exist in the database
        String shortCode = response.substring("Short URL: http://localhost:8080/".length());
        assertTrue(urlRepository.existsByShortCode(shortCode), 
                   "Short code should exist in the database");
    }

    @Test
    void testShortenUrlWithoutHttpPrefix() throws Exception {
        // Given: A URL without http prefix
        String inputUrl = "google.com";
        String expectedNormalizedUrl = "http://google.com";
        urlrecord request = new urlrecord(inputUrl);

        // When: We call the shorten endpoint
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then: The normalized URL should be saved in the database
        assertTrue(urlRepository.findByOriginalUrl(expectedNormalizedUrl).isPresent(), 
                   "Normalized URL should be saved in the database");
    }

    @Test
    void testShortenSameUrlTwiceReturnsExistingShortCode() throws Exception {
        // Given: A URL to shorten
        String originalUrl = "https://stackoverflow.com";
        urlrecord request = new urlrecord(originalUrl);

        // When: We call the shorten endpoint twice
        String response1 = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then: Both responses should be identical
        assertEquals(response1, response2, "Same URL should return the same short code");
        
        // And: Only one entry should exist in the database
        assertEquals(1, urlRepository.findAll().stream()
                .filter(url -> url.getOriginalUrl().equals(originalUrl))
                .count(), "Only one entry should exist for the same URL");
    }
}