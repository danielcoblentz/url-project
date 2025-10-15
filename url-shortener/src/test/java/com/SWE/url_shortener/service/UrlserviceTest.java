package com.SWE.url_shortener.service;

import com.SWE.url_shortener.model.url;
import com.SWE.url_shortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class UrlserviceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private Urlservice urlService;

    @Test
    void testShortenUrl_NewUrl() {
        // Given
        String originalUrl = "https://example.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String shortCode = urlService.shortenUrl(originalUrl);

        // Then
        assertNotNull(shortCode);
        assertEquals(7, shortCode.length());
        verify(urlRepository).save(any(url.class));
        verify(urlRepository).findByOriginalUrl(originalUrl);
    }

    @Test
    void testShortenUrl_ExistingUrl() {
        // Given
        String originalUrl = "https://example.com";
        String existingShortCode = "abc123x";
        url existingUrl = new url();
        existingUrl.setOriginalUrl(originalUrl);
        existingUrl.setShortCode(existingShortCode);
        
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));

        // When
        String shortCode = urlService.shortenUrl(originalUrl);

        // Then
        assertEquals(existingShortCode, shortCode);
        verify(urlRepository, never()).save(any(url.class));
    }

    @Test
    void testShortenUrl_NormalizesUrl() {
        // Given
        String originalUrl = "example.com";
        String expectedNormalizedUrl = "http://example.com";
        when(urlRepository.findByOriginalUrl(expectedNormalizedUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        urlService.shortenUrl(originalUrl);

        // Then
        verify(urlRepository).findByOriginalUrl(expectedNormalizedUrl);
    }

    @Test
    void testGetOriginalUrl_Found() {
        // Given
        String shortCode = "abc123x";
        String originalUrl = "https://example.com";
        url urlEntity = new url();
        urlEntity.setShortCode(shortCode);
        urlEntity.setOriginalUrl(originalUrl);
        
        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(urlEntity));

        // When
        String result = urlService.getOriginalUrl(shortCode);

        // Then
        assertEquals(originalUrl, result);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        // Given
        String shortCode = "nonexistent";
        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        String result = urlService.getOriginalUrl(shortCode);

        // Then
        assertNull(result);
    }

    @Test
    void testShortenUrl_GeneratesUniqueCode() {
        // Given
        String originalUrl = "https://example.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
    // Simulate two failed attempts due to unique constraint on flush(), then succeed.
    when(urlRepository.save(any(url.class))).thenAnswer(invocation -> invocation.getArgument(0));
    doThrow(new DataIntegrityViolationException("unique collision"))
        .doThrow(new DataIntegrityViolationException("unique collision"))
        .doNothing()
        .when(urlRepository).flush();

        // When
        String shortCode = urlService.shortenUrl(originalUrl);

        // Then
        assertNotNull(shortCode);
        // save + flush should have been attempted three times (2 failures, 1 success)
        verify(urlRepository, times(3)).save(any(url.class));
        verify(urlRepository, times(3)).flush();
    }

    @Test
    void testNormalize_AddsHttpPrefix() {
        // Given
        String originalUrl = "google.com";
        when(urlRepository.findByOriginalUrl("http://google.com")).thenReturn(Optional.empty());
        when(urlRepository.save(any(url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        urlService.shortenUrl(originalUrl);

        // Then
        verify(urlRepository).findByOriginalUrl("http://google.com");
    }

    @Test
    void testNormalize_PreservesHttpsPrefix() {
        // Given
        String originalUrl = "https://secure.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        urlService.shortenUrl(originalUrl);

        // Then
        verify(urlRepository).findByOriginalUrl(originalUrl);
    }
}