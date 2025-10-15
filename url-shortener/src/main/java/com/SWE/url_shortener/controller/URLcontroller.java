package com.SWE.url_shortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.SWE.url_shortener.service.Urlservice;

import java.net.URI;

record urlrecord(String url) {}

@Controller
public class URLcontroller {
    private final Urlservice urlService;

    public URLcontroller(Urlservice urlService) {
        this.urlService = urlService;
    }

    // POST /shorten  -> returns short url plus the prefix 
    @PostMapping("/shorten")
    @ResponseBody
    public ResponseEntity<String> shortenString(@RequestBody urlrecord req) {
        try {
            if (req == null || req.url() == null || req.url().isBlank()) {
                return ResponseEntity.badRequest().body("Error: url must not be empty");
            }
            String shortCode = urlService.shortenUrl(req.url());
            return ResponseEntity.ok("Short URL: http://localhost:8080/" + shortCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{shortCode:[A-Za-z0-9_-]{4,64}}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Prevent the browser's automatic /favicon.ico request from hitting the redirect mapping
    @GetMapping("/favicon.ico")
    @ResponseBody
    public void noFavicon() {
    }
}
