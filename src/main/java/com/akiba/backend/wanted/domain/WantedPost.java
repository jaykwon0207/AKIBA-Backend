package com.akiba.backend.wanted.domain;

import com.akiba.backend.used.domain.MarketPost;
import jakarta.persistence.*;
import lombok.*;

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

    private String conditionTxt; //희망 상태

    public void update(String conditionTxt) {
        if (conditionTxt != null) this.conditionTxt = conditionTxt;
    }

}
