package com.SWE.url_shortener.service;

import com.SWE.url_shortener.model.Url;
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
        // new URL to shorten
        String originalUrl = "https://example.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // shorten it
        String shortCode = urlService.shortenUrl(originalUrl);

        // check result
        assertNotNull(shortCode);
        assertEquals(7, shortCode.length());
        verify(urlRepository).save(any(Url.class));
        verify(urlRepository).findByOriginalUrl(originalUrl);
    }

    @Test
    void testShortenUrl_ExistingUrl() {
        // existing URL
        String originalUrl = "https://example.com";
        String existingShortCode = "abc123x";
        Url existingUrl = new Url();
        existingUrl.setOriginalUrl(originalUrl);
        existingUrl.setShortCode(existingShortCode);
        
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));

        // should return existing code
        String shortCode = urlService.shortenUrl(originalUrl);

        assertEquals(existingShortCode, shortCode);
        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void testShortenUrl_NormalizesUrl() {
        // URL without protocol
        String originalUrl = "example.com";
        String expectedNormalizedUrl = "http://example.com";
        when(urlRepository.findByOriginalUrl(expectedNormalizedUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        urlService.shortenUrl(originalUrl);

        // should normalize URL
        verify(urlRepository).findByOriginalUrl(expectedNormalizedUrl);
    }

    @Test
    void testGetOriginalUrl_Found() {
        // existing short code
        String shortCode = "abc123x";
        String originalUrl = "https://example.com";
        Url urlEntity = new Url();
        urlEntity.setShortCode(shortCode);
        urlEntity.setOriginalUrl(originalUrl);
        
        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getOriginalUrl(shortCode);

        assertEquals(originalUrl, result);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        // nonexistent short code
        String shortCode = "nonexistent";
        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        String result = urlService.getOriginalUrl(shortCode);

        assertNull(result);
    }

    @Test
    void testShortenUrl_GeneratesUniqueCode() {
        // test collision handling
        String originalUrl = "https://example.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new DataIntegrityViolationException("unique collision"))
            .doThrow(new DataIntegrityViolationException("unique collision"))
            .doNothing()
            .when(urlRepository).flush();

        String shortCode = urlService.shortenUrl(originalUrl);

        // should retry after collisions
        assertNotNull(shortCode);
        verify(urlRepository, times(3)).save(any(Url.class));
        verify(urlRepository, times(3)).flush();
    }

    @Test
    void testNormalize_AddsHttpPrefix() {
        // URL without http
        String originalUrl = "google.com";
        when(urlRepository.findByOriginalUrl("http://google.com")).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        urlService.shortenUrl(originalUrl);

        verify(urlRepository).findByOriginalUrl("http://google.com");
    }

    @Test
    void testNormalize_PreservesHttpsPrefix() {
        // HTTPS URL should stay unchanged
        String originalUrl = "https://secure.com";
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        urlService.shortenUrl(originalUrl);

        verify(urlRepository).findByOriginalUrl(originalUrl);
    }
}