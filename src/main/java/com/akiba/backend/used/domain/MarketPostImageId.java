package com.akiba.backend.used.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MarketPostImageId implements Serializable {
    private Long postId;
    private Long mediaId;
}

