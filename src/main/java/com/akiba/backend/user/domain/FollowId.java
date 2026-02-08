package com.akiba.backend.user.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FollowId implements Serializable {
    private Long followerId;
    private Long followingId;
}
