package com.akiba.backend.user.dto;

import lombok.Getter;

@Getter
public class UpdateUserRequest {
    private String nickname;
    private String bio;
    private Long profileImageMediaId;
}