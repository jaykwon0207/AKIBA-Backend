package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuctionPost {

    @Id
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private MarketPost marketPost;

    private int startPrice;
    private Integer buyNowPrice;
    private int bidStep;
    private LocalDateTime endsAt;
    private Long winnerUserId;
    private Integer finalPrice;
}
