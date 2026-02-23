package com.akiba.backend.user.controller;

import com.akiba.backend.user.dto.LoginRequest;
import com.akiba.backend.user.dto.LoginResponse;
import com.akiba.backend.user.dto.NicknameRequest;
import com.akiba.backend.user.dto.NicknameResponse;
import com.akiba.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<NicknameResponse> updateNickname(
            @RequestBody NicknameRequest request,
            @AuthenticationPrincipal Long userId) {
        NicknameResponse response = userService.updateNickname(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestParam String nickname) {
        boolean available = userService.checkNickname(nickname);
        String message = available ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return ResponseEntity.ok(Map.of("available", available, "message", message));
    }



}