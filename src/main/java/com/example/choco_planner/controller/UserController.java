package com.example.choco_planner.controller;

import com.example.choco_planner.controller.dto.request.LoginRequestDTO;
import com.example.choco_planner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequestDTO request
    ) {
        Long userId = userService.login(request.getEmail());
        return ResponseEntity.ok(userId.toString());
    }

//    // 회원가입 엔드포인트
//    @PostMapping("/signup")
//    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO request) {
//        String result = userService.signUp(request.getUsername(), request.getPassword());
//
//        if ("Sign-up successful".equals(result)) {
//            return ResponseEntity.status(HttpStatus.CREATED).body(result);
//        } else {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
//        }
//    }
}
