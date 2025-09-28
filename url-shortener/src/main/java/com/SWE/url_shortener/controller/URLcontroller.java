package com.SWE.url_shortener.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//DTO for accepting input from user
record urlrecord(String url) {
}

@RestController
public class URLcontroller {
    @PostMapping("/shorten")
    public String shortenString(@RequestBody urlrecord req) {
        // Very simple "shortener" for MVP
        String code = Integer.toHexString(req.url().hashCode());
        return "Short URL: http://localhost:8080/" + code;
    }

    @GetMapping("/{code:[a-zA-Z0-9]+}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        // just a sample url (we dont have any loaded into database yet)
        String originalUrl = "https://example.com";
        // after we have data look it up in DB with UrlRepository
        return ResponseEntity.status(HttpStatus.FOUND) // 302 redirect
                .location(URI.create(originalUrl))
                .build();
    }

    // delete this later its just for testing
    @GetMapping("/testing")
    public String testMethod() {
        return "its working!";
    }
}
