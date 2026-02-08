package com.akiba.backend.market.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false, length = 50, unique = true)
    private String name;
}
