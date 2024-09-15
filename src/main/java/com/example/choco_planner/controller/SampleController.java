package com.example.choco_planner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "sample", description = "Sample API")
@RestController
@RequestMapping("/sample")
public class SampleController {

    @Operation(summary = "Sample API", description = "Sample API")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
