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

}