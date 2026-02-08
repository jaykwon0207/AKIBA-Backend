package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "market_post_images")
@IdClass(MarketPostImageId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostImage {

    @Id
    private Long postId;

    @Id
    private Long mediaId;

    @Column(nullable = false)
    private int sortOrder;
}
