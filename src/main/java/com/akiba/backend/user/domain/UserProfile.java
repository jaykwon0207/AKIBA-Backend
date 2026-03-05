package com.akiba.backend.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile {

    private String profileImageUrl; // 필드명이 정확히 일치해야 합니다

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 500)
    private String bio;

    private Long profileImageMediaId;

    @Column(nullable = false)
    private Double mannerScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.mannerScore == null) {
            this.mannerScore = 36.5; // 기본값
        }
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateProfileImage(Long profileImageMediaId) {
        this.profileImageMediaId = profileImageMediaId;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
