package com.akiba.backend.board.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_post_tags")
@IdClass(BoardPostTagId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardPostTag {

    @Id
    private Long postId;

    @Id
    private Long tagId;
}
