package com.SWE.url_shortener.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.SWE.url_shortener.service.Urlservice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.net.URI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


//DTO for accepting input from user
record urlrecord(String url) {
}

@Controller
public class URLcontroller {
    private final Urlservice urlService;

    public URLcontroller(Urlservice urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    @ResponseBody
    public String shortenString(@RequestBody urlrecord req) {
        String shortCode = urlService.shortenUrl(req.url());
        return "Short URL: http://localhost:8080/" + shortCode;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        
        if (originalUrl == null) {
            // Short code not found, return 404
            return ResponseEntity.notFound().build();
        }
        
        try {
            // Redirect to the original URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (IllegalArgumentException e) {
            // Invalid URL format, return 400 Bad Request
            return ResponseEntity.badRequest().build();
        }
    }
}
