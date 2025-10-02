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

    // Contract: input originalUrl (may be missing scheme), returns shortCode
    public String shortenUrl(String originalUrl) {
        String normalized = normalize(originalUrl);

        // If original URL already exists, return existing short code
        return urlRepository.findByOriginalUrl(normalized)
                .map(url::getShortCode)
                .orElseGet(() -> {
                    String shortCode;
                    do {
                        shortCode = generateShortCode();
                    } while (urlRepository.existsByShortCode(shortCode));

                    url newUrl = new url();
                    newUrl.setOriginalUrl(normalized);
                    newUrl.setShortCode(shortCode);
                    urlRepository.save(newUrl);
                    return shortCode;
                });
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(characters[secureRandom.nextInt(characters.length)]);
        }
        return sb.toString();
    }

    private String normalize(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
}
