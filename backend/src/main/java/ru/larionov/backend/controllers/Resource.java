package ru.larionov.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resource")
public class Resource {

    @GetMapping
    private String getResource() {
        return "This is defenced resource";
    }
}
