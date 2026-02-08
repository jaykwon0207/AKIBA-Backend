package com.akiba.backend.market.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MarketPostFavoriteId implements Serializable {
    private Long postId;
    private Long userId;
}