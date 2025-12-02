package com.SWE.url_shortener.controller;

import com.SWE.url_shortener.service.Urlservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;

record UrlRequest(String url) {}

@Controller
@RequestMapping({"/api/url", ""})
public class URLcontroller {

    private final Urlservice urlService;

    public URLcontroller(Urlservice urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    @ResponseBody
    public ResponseEntity<String> shortenString(@RequestBody UrlRequest request) {
        if (request == null || request.url() == null || request.url().isBlank()) {
            return ResponseEntity.badRequest().body("Error: url must not be empty");
        }
        try {
            String shortCode = urlService.shortenUrl(request.url());
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

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    // Avoid handling automatic browser favicon request with the redirect mapping.
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> noFavicon() {
        return ResponseEntity.ok().build();
    }
}
