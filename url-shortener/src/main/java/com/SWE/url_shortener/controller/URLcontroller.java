package com.SWE.url_shortener.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



//DTO for accepting input from user
record urlrecord(String url) {}


@RestController
public class URLcontroller {
@PostMapping("/shorten")
public String shortenString(@RequestBody urlrecord req) {
    //need to make the shortenURL method to send confirmaion to user after entering
    return "short url created";
}

@GetMapping("/testing")
public String testMethod() {
    return "its wokring!";
}


    
}
