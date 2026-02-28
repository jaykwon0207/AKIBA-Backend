package com.akiba.backend.used.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "market_post_tags")
@IdClass(MarketPostTagId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostTag {

    @Id
    private Long postId;

    @Id
    private Long tagId;
}
