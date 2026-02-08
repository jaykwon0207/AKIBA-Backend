package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "market_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MarketPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketPostType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCondition productCondition;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer price;

    private String locationTxt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketPostStatus status;

    @Column(nullable = false)
    private int viewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.status = MarketPostStatus.ACTIVE;
        this.viewCount = 0;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
