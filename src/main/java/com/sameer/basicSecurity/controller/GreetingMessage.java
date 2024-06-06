package com.sameer.basicSecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GreetingMessage {
    @GetMapping("/hello")
    public String greetingMessage(){
        return "Hello Sameer";
    }
}
