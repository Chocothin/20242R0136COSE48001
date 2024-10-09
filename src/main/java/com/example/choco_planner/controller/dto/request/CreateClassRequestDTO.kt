package com.example.choco_planner.controller.dto.request

data class CreateClassRequestDTO(
    val userId: Int,
    val title: String,
    val description: String?
)
