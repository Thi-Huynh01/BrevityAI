package com.brevityai.backend.practice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// May turn into health check class
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Brevity AI Backend is running!";
    }
}
