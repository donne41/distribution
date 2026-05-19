package com.chatting.service2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController2 {


    @GetMapping("/api/test")
    public String test() {
        return "hello from Service 2! ";
    }
}
