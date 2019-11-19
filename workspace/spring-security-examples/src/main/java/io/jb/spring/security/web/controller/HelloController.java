package io.jb.spring.security.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String greet() {
        return "<h1>Hello!</h1>";
    }

    @GetMapping("/user")
    public String user() {
        return "<h1>Hello User!</h1>";
    }

    @GetMapping("/admin")
    public String admin() {
        return "<h1>Hello Admin!</h1>";
    }

}
