package com.example.choco_planner.controller;

import com.example.choco_planner.storage.entity.SampleEntity;
import com.example.choco_planner.storage.repository.SampleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "sample", description = "Sample API")
@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleController {

    private final SampleRepository sampleRepository;

    @Operation(summary = "Sample API", description = "Sample API")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @Operation(summary = "Insert Name", description = "Insert Name")
    @PostMapping("/name")
    public String name(@RequestBody String name) {
        SampleEntity sampleEntity = new SampleEntity(name);
        sampleRepository.save(sampleEntity);
        return "Name saved successfully!";

    }
}
