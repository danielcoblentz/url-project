package com.SWE.url_shortener.repository;

import com.SWE.url_shortener.model.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void testFindByShortCode_Found() {
        // Given
        Url testUrl = new Url();
        testUrl.setOriginalUrl("https://example.com");
        testUrl.setShortCode("abc123x");
        testUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(testUrl);

        // When
        Optional<Url> result = urlRepository.findByShortCode("abc123x");

        // Then
        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get().getOriginalUrl());
        assertEquals("abc123x", result.get().getShortCode());
    }

    @Test
    void testFindByShortCode_NotFound() {
        // When
        Optional<Url> result = urlRepository.findByShortCode("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByOriginalUrl_Found() {
        // Given
        Url testUrl = new Url();
        testUrl.setOriginalUrl("https://github.com");
        testUrl.setShortCode("xyz789a");
        testUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(testUrl);

        // When
        Optional<Url> result = urlRepository.findByOriginalUrl("https://github.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("xyz789a", result.get().getShortCode());
    }

    @Test
    void testFindByOriginalUrl_NotFound() {
        // When
        Optional<Url> result = urlRepository.findByOriginalUrl("https://notfound.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByShortCode_True() {
        // Given
        Url testUrl = new Url();
        testUrl.setOriginalUrl("https://stackoverflow.com");
        testUrl.setShortCode("def456y");
        testUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(testUrl);

        // When
        boolean exists = urlRepository.existsByShortCode("def456y");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByShortCode_False() {
        // When
        boolean exists = urlRepository.existsByShortCode("doesnotexist");

        // Then
        assertFalse(exists);
    }

    @Test
    void testShortCodeUniqueness() {
        // Given
        Url firstUrl = new Url();
        firstUrl.setOriginalUrl("https://first.com");
        firstUrl.setShortCode("unique1");
        firstUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(firstUrl);

        // When/Then - Trying to save another URL with same short code should fail
        Url secondUrl = new Url();
        secondUrl.setOriginalUrl("https://second.com");
        secondUrl.setShortCode("unique1"); // Same short code
        secondUrl.setCreatedTime(LocalDateTime.now());

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(secondUrl);
        });
    }

    @Test
    void testSaveAndRetrieve() {
        // Given
        Url testUrl = new Url();
        testUrl.setOriginalUrl("https://test.com");
        testUrl.setShortCode("test123");
        testUrl.setCreatedTime(LocalDateTime.now());

        // When
        Url savedUrl = urlRepository.save(testUrl);

        // Then
        assertNotNull(savedUrl.getId());
        assertEquals("https://test.com", savedUrl.getOriginalUrl());
        assertEquals("test123", savedUrl.getShortCode());
        assertNotNull(savedUrl.getCreatedTime());

        // Verify it can be retrieved
        Optional<Url> retrieved = urlRepository.findById(savedUrl.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(savedUrl.getOriginalUrl(), retrieved.get().getOriginalUrl());
    }
}