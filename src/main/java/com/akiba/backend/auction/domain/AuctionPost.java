package com.akiba.backend.auction.domain;

import com.akiba.backend.used.domain.MarketPost;
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

    @Column(nullable = false)
    @Builder.Default
    private int bidCount = 0;           // 입찰 수

    public void update(int startPrice, Integer buyNowPrice, int bidStep, LocalDateTime endsAt) {
        this.startPrice = startPrice;
        if (buyNowPrice != null) this.buyNowPrice = buyNowPrice;
        this.bidStep = bidStep;
        if (endsAt != null) this.endsAt = endsAt;
    }

    // 입찰 수 증가
    public void increaseBidCount() {
        this.bidCount++;
    }

    // 경매 종료 처리 (낙찰)
    public void endAuction(Long winnerUserId, int finalPrice) {
        this.winnerUserId = winnerUserId;
        this.finalPrice = finalPrice;
    }

}
