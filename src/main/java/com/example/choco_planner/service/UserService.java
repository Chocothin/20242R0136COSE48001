package com.example.choco_planner.service;

import com.example.choco_planner.common.utils.JwtUtil;
import com.example.choco_planner.storage.entity.UserEntity;
import com.example.choco_planner.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Long login(String email) {
        //email 디비에 있으면 id 리턴, 없으면 생성하고 id 리턴
        UserEntity userEntity = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .name(email)
                    .build();
            return userRepository.save(newUser);
        });

        return userEntity.getId();
    }


//    // 회원가입 메서드
//    @Transactional
//    public String signUp(String username, String password) {
//        if (userRepository.findByUsername(username).isPresent()) {
//            return "Username already exists";
//        }
//
//        UserEntity newUser = UserEntity.builder()
//                .username(username)
//                .name(username)
//                .password(password)
//                .build();
//
//        userRepository.save(newUser);
//        return "Sign-up successful";
//    }
}
