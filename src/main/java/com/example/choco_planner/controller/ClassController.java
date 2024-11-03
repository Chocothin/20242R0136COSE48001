package com.example.choco_planner.controller;

import com.example.choco_planner.common.aop.AuthPrincipal;
import com.example.choco_planner.common.domain.CustomUser;
import com.example.choco_planner.controller.dto.request.CreateClassRequestDTO;
import com.example.choco_planner.controller.dto.response.ClassResponseDTO;
import com.example.choco_planner.service.ClassService;
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


    @PostMapping("")
    public ResponseEntity<ClassResponseDTO> createClass(
            @RequestBody CreateClassRequestDTO request,
            @AuthPrincipal CustomUser customUser
    ) {
        ClassResponseDTO response = classService.createClass(request, customUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 특정 사용자의 모든 수업 조회
    @GetMapping("")
    public ResponseEntity<List<ClassResponseDTO>> getClassesByUserId(
            @AuthPrincipal CustomUser customUser
    ) {
        List<ClassResponseDTO> response = classService.getClassesByUserId(customUser.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //특정 수업 조회
    @GetMapping("{classId}")
    public ResponseEntity<ClassResponseDTO> getClassByUserIdAndClassId(
            @PathVariable Long classId,
            @AuthPrincipal CustomUser customUser
    ) {

        Optional<ClassResponseDTO> response = classService.getClassByUserIdAndClassId(customUser.getId(), classId);
        return response.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
