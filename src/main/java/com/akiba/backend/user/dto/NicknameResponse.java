package com.akiba.backend.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NicknameResponse {
    private Long userId;
    private String nickname;
}