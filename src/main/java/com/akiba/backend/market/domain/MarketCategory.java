package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "market_categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private Long parentId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int sortOrder;
}
