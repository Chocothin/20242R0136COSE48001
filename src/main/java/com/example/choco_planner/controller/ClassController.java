package com.example.choco_planner.controller;

import com.example.choco_planner.controller.dto.request.CreateClassRequestDTO;
import com.example.choco_planner.controller.dto.response.ClassResponseDTO;
import com.example.choco_planner.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Class", description = "Class API")
@RestController
@RequestMapping("/api/class")
public class ClassController{
    private final ClassService classService;

    public ClassController(ClassService classService){
        this.classService = classService;
    }


    @Operation(summary = "수업 생성")
    @PostMapping("{userId}")
    public ResponseEntity<ClassResponseDTO> createClass(
            @PathVariable Long userId,
            @RequestBody CreateClassRequestDTO request
    ) {
        ClassResponseDTO response = classService.createClass(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 특정 사용자의 모든 수업 조회
    @Operation(summary = "특정 사용자의 모든 수업 조회")
    @GetMapping("user/{userId}")
    public ResponseEntity<List<ClassResponseDTO>> getClassesByUserId(
            @PathVariable Long userId
    ) {
        List<ClassResponseDTO> response = classService.getClassesByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //특정 수업 조회
    @Operation(summary = "특정 수업 조회")
    @GetMapping("{classId}")
    public ResponseEntity<ClassResponseDTO> getClassByUserIdAndClassId(
            @PathVariable Long classId
    ) {

        Optional<ClassResponseDTO> response = classService.getClassByClassId(classId);
        return response.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
