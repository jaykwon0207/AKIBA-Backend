package com.akiba.backend.used.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "market_post_favorites")
@IdClass(MarketPostFavoriteId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostFavorite {

    @Id
    private Long postId;

    @Id
    private Long userId;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}