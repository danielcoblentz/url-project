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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        // shorten a URL
        String originalUrl = "https://example.com";
        urlrecord request = new urlrecord(originalUrl);

        // call the endpoint
        String response = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // check database has the URL
        assertTrue(urlRepository.findByOriginalUrl(originalUrl).isPresent(), 
                   "URL should be saved in the database");
        
        // check response format
        assertTrue(response.startsWith("Short URL: http://localhost:8080/"), 
                   "Response should contain the short URL");
        
        // check short code exists
        String shortCode = response.substring("Short URL: http://localhost:8080/".length());
        assertTrue(urlRepository.existsByShortCode(shortCode), 
                   "Short code should exist in the database");
    }

    @Test
    void testShortenUrlWithoutHttpPrefix() throws Exception {
        // URL without http prefix
        String inputUrl = "google.com";
        String expectedNormalizedUrl = "http://google.com";
        urlrecord request = new urlrecord(inputUrl);

        // call endpoint
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // check normalized URL is saved
        assertTrue(urlRepository.findByOriginalUrl(expectedNormalizedUrl).isPresent(), 
                   "Normalized URL should be saved in the database");
    }

    @Test
    void testShortenSameUrlTwiceReturnsExistingShortCode() throws Exception {
        // same URL twice
        String originalUrl = "https://stackoverflow.com";
        urlrecord request = new urlrecord(originalUrl);

        // call endpoint twice
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

        // should return same short code
        assertEquals(response1, response2, "Same URL should return the same short code");
        
        // only one database entry
        assertEquals(1, urlRepository.findAll().stream()
                .filter(url -> url.getOriginalUrl().equals(originalUrl))
                .count(), "Only one entry should exist for the same URL");
    }

    @Test
    void testRedirectToOriginalUrl() throws Exception {
        // shorten a URL first
        String originalUrl = "https://github.com";
        urlrecord request = new urlrecord(originalUrl);

        String response = mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // extract short code
        String shortCode = response.substring("Short URL: http://localhost:8080/".length());

        // test redirect
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void testRedirectWithInvalidShortCode() throws Exception {
        // invalid short code should return 404
        mockMvc.perform(get("/invalidcode123"))
                .andExpect(status().isNotFound());
    }
}