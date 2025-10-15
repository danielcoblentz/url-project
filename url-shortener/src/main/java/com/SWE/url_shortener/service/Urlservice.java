package com.SWE.url_shortener.service;

import com.SWE.url_shortener.repository.UrlRepository;
import com.SWE.url_shortener.model.url;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class Urlservice {
    private final UrlRepository urlRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int CODE_LENGTH = 7;

    public Urlservice(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // returns shortCode
    public String shortenUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        String normalized = normalize(originalUrl);

        // If original URL already exists, return existing short code
        return urlRepository.findByOriginalUrl(normalized)
                .map(url::getShortCode)
                .orElseGet(() -> {

                    final int MAX_ATTEMPTS = 6; 
                    for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                        String shortCode = generateShortCode();
                        url newUrl = new url();
                        newUrl.setOriginalUrl(normalized);
                        newUrl.setShortCode(shortCode);
                        try {
                            url saved = urlRepository.save(newUrl);
                           
                            urlRepository.flush();
                            return saved.getShortCode();
                        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
        
                        } 
                    }
                    throw new IllegalStateException("Unable to generate a unique short code after " + MAX_ATTEMPTS + " attempts");
                });
    }

    // returns original URL for a given short code
    public String getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .map(url::getOriginalUrl)
                .orElse(null);
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(characters[secureRandom.nextInt(characters.length)]);
        }
        return sb.toString();
    }

    private String normalize(String url) {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
}
