package com.example.choco_planner.service;

import com.example.choco_planner.common.utils.JwtUtil;
import com.example.choco_planner.storage.entity.UserEntity;
import com.example.choco_planner.storage.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 로그인 메서드: 사용자명과 비밀번호를 확인하여 JWT 토큰 반환
    public String login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> jwtUtil.generateToken(user.getId(), user.getUsername(), user.getName())) // JWT 토큰 생성
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }


    // 회원가입 메서드
    @Transactional
    public String signUp(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists";
        }

        UserEntity newUser = UserEntity.builder()
                .username(username)
                .name(username)
                .password(password)
                .build();

        userRepository.save(newUser);
        return "Sign-up successful";
    }
}
