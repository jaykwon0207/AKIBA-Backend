package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_bids")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionBid {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    private Long postId;
    private Long userId;
    private int bidPrice;
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
