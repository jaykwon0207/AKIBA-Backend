package com.akiba.backend.board.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardPostTagId implements Serializable {

    private Long postId;
    private Long tagId;
}
