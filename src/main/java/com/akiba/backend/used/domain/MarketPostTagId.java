package com.akiba.backend.used.domain;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MarketPostTagId implements Serializable {
    private Long postId;
    private Long tagId;
}

