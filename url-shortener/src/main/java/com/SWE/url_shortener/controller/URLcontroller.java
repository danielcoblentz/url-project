package com.SWE.url_shortener.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.SWE.url_shortener.service.Urlservice;
import org.springframework.web.bind.annotation.GetMapping;

//DTO for accepting input from user
record urlrecord(String url) {
}

@RestController
public class URLcontroller {
    private final Urlservice urlService;

    public URLcontroller(Urlservice urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public String shortenString(@RequestBody urlrecord req) {
        String shortCode = urlService.shortenUrl(req.url());
        return "Short URL: http://localhost:8080/" + shortCode;
    }

    // @GetMapping("/{code:[a-zA-Z0-9]+}")
    // public ResponseEntity<Void> redirect(@PathVariable String code) {
    //     // get the prev origional URL from the DB then redirect to it

    // }

    // delete this later its just for testing
    @GetMapping("/testing")
    public String testMethod() {
        return "its working!";
    }
}
