package com.SWE.url_shortener.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Simple record for testing
record TestUrlRecord(String url) {}

@SpringBootTest
@AutoConfigureMockMvc
class PerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testShortenUrl_ResponseTime() throws Exception {
        // Given
        TestUrlRecord request = new TestUrlRecord("https://performance-test.com");
        
        // When
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Then: Should respond within reasonable time (less than 1 second)
        assertTrue(responseTime < 1000, 
                   "Response time should be less than 1000ms, was: " + responseTime + "ms");
    }

    @Test
    void testConcurrentRequests() throws Exception {
        // Given: Multiple concurrent requests
        int numberOfRequests = 10;
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] futures = new CompletableFuture[numberOfRequests];
        
        // When: Execute requests concurrently
        for (int i = 0; i < numberOfRequests; i++) {
            final int requestId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    TestUrlRecord request = new TestUrlRecord("https://concurrent-test-" + requestId + ".com");
                    mockMvc.perform(post("/shorten")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        
        // Then: All requests should complete successfully
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures);
        assertDoesNotThrow(() -> allFutures.get(10, TimeUnit.SECONDS));
    }

    @Test
    void testBulkUrlShortening() throws Exception {
        // Given: Large number of URLs to shorten
        int numberOfUrls = 50;
        long startTime = System.currentTimeMillis();
        
        // When: Shorten many URLs
        for (int i = 0; i < numberOfUrls; i++) {
            TestUrlRecord request = new TestUrlRecord("https://bulk-test-" + i + ".com");
            mockMvc.perform(post("/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double averageTime = (double) totalTime / numberOfUrls;
        
        // Then: Average time per request should be reasonable
        assertTrue(averageTime < 100, 
                   "Average time per request should be less than 100ms, was: " + averageTime + "ms");
    }
}