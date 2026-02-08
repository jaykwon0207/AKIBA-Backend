package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wanted_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WantedPost {

    @Id
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private MarketPost marketPost;

    private Integer desiredPrice;
    private String conditionTxt;
    private LocalDateTime deadlineAt;
}
