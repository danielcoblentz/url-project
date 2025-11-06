package com.SWE.url_shortener.repository;

import com.SWE.url_shortener.model.url;
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
        // save a URL
        url testUrl = new url();
        testUrl.setOriginalUrl("https://example.com");
        testUrl.setShortCode("abc123x");
        testUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(testUrl);

        // find by short code
        Optional<url> result = urlRepository.findByShortCode("abc123x");

        // check result
        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get().getOriginalUrl());
        assertEquals("abc123x", result.get().getShortCode());
    }

    @Test
    void testFindByShortCode_NotFound() {
        // nonexistent code should return empty
        Optional<url> result = urlRepository.findByShortCode("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByOriginalUrl_Found() {
        // save a URL
        url testUrl = new url();
        testUrl.setOriginalUrl("https://github.com");
        testUrl.setShortCode("xyz789a");
        testUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(testUrl);

        // find by original URL
        Optional<url> result = urlRepository.findByOriginalUrl("https://github.com");

        // check result
        assertTrue(result.isPresent());
        assertEquals("xyz789a", result.get().getShortCode());
    }

    @Test
    void testFindByOriginalUrl_NotFound() {
        // nonexistent URL should return empty
        Optional<url> result = urlRepository.findByOriginalUrl("https://notfound.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByShortCode_True() {
        // save a URL
        url testUrl = new url();
        testUrl.setOriginalUrl("https://stackoverflow.com");
        testUrl.setShortCode("def456y");
        testUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(testUrl);

        // check if exists
        boolean exists = urlRepository.existsByShortCode("def456y");

        assertTrue(exists);
    }

    @Test
    void testExistsByShortCode_False() {
        // nonexistent code should return false
        boolean exists = urlRepository.existsByShortCode("doesnotexist");

        assertFalse(exists);
    }

    @Test
    void testShortCodeUniqueness() {
        // save first URL
        url firstUrl = new url();
        firstUrl.setOriginalUrl("https://first.com");
        firstUrl.setShortCode("unique1");
        firstUrl.setCreatedTime(LocalDateTime.now());
        entityManager.persistAndFlush(firstUrl);

        // duplicate short code should fail
        url secondUrl = new url();
        secondUrl.setOriginalUrl("https://second.com");
        secondUrl.setShortCode("unique1");
        secondUrl.setCreatedTime(LocalDateTime.now());

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(secondUrl);
        });
    }

    @Test
    void testSaveAndRetrieve() {
        // save a URL
        url testUrl = new url();
        testUrl.setOriginalUrl("https://test.com");
        testUrl.setShortCode("test123");
        testUrl.setCreatedTime(LocalDateTime.now());

        url savedUrl = urlRepository.save(testUrl);

        // check saved data
        assertNotNull(savedUrl.getId());
        assertEquals("https://test.com", savedUrl.getOriginalUrl());
        assertEquals("test123", savedUrl.getShortCode());
        assertNotNull(savedUrl.getCreatedTime());

        // check retrieval
        Optional<url> retrieved = urlRepository.findById(savedUrl.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(savedUrl.getOriginalUrl(), retrieved.get().getOriginalUrl());
    }
}