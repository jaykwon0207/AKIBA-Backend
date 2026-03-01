package com.akiba.backend.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_recent_views",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_recent_views_user_post", columnNames = {"user_id", "post_id"})
        },
        indexes = {
                @Index(name = "idx_user_recent_views_user_viewed_at", columnList = "user_id, viewed_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRecentView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    void prePersist() {
        if (this.viewedAt == null) {
            this.viewedAt = LocalDateTime.now();
        }
    }

    public void touch() {
        this.viewedAt = LocalDateTime.now();
    }
}
