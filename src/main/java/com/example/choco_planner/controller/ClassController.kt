package com.example.choco_planner.controller

import com.example.choco_planner.controller.dto.request.CreateClassRequestDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/class")
class ClassController {

    @PostMapping("/")
    fun createClass(
        @RequestBody request: CreateClassRequestDTO
    ): ResponseEntity<Any> {
        return ResponseEntity.ok("Class created")

    }

    @GetMapping("/")
    fun getClasses(): ResponseEntity<Any> {
        return ResponseEntity.ok("Classes")
    }
}
