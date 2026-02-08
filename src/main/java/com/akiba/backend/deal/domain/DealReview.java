package com.akiba.backend.deal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deal_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DealReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private Long dealId;

    @Column(nullable = false)
    private Long reviewerId;

    @Column(nullable = false)
    private Long revieweeId;

    @Column(nullable = false)
    private int rating; // 1~5

    @Column(length = 500)
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
