package com.akiba.backend.user.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserInfoResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String provider;
    private String status;
    private LocalDateTime createdAt;
    private String bio;
    private Long profileImageMediaId;
}