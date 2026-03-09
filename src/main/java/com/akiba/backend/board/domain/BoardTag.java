package com.akiba.backend.board.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false, length = 50, unique = true)
    private String name;
}
