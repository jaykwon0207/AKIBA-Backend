package com.akiba.backend.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserResponse {
    private Long userId;
    private String nickname;
    private String bio;
    private Long profileImageMediaId;
    private String message;
}