package com.SWE.url_shortener.service;

import com.SWE.url_shortener.repository.UrlRepository; 
import com.SWE.url_shortener.model.url; 
import java.util.Optional;
import java.security.SecureRandom;



// need to od the folowing:
// 1: check if the original url already exists in the database
//  1.2: if it does, return the existing short code
// 2: if it does not exist, generate a new short code
// 3: save the new original url and short code to the database
public class Urlservice {
    private final UrlRepository urlRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private final static int code_length = 7;

    public Urlservice(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String shortenUrl(String originalUrl) {
        // Check if the original URL already exists in the database
        return urlRepository.findByOriginalUrl(originalUrl).orElseGet(String code = generateUniqueCode()));
        return urlRepository.save(new urlMapping(code, normalize(originalUrl)));
        }

        // generate a new short code
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (urlRepository.existsByShortCode(shortCode));

        // Step 3: Save the new original URL and short code to the database
        url newUrl = new url();
        newUrl.setOriginalUrl(originalUrl);
        newUrl.setShortCode(shortCode);
        urlRepository.save(newUrl);

        return shortCode;
    }}

    private String generateShortCode() {
        StringBuilder shortCode = new StringBuilder(code_length);
        for (int i = 0; i < code_length; i++) {
            shortCode.append(characters[secureRandom.nextInt(characters.length)]);
        }
        return shortCode.toString();
    }

    private String normalize(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
}}
