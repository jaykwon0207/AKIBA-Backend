package com.akiba.backend.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String saleOrAuctionLink;

    private int likeCount;
    private int commentCount;
    private int authenticVoteCount;
    private int fakeVoteCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void updatePost(String title, String content, String saleOrAuctionLink) {
        this.title = title;
        this.content = content;
        this.saleOrAuctionLink = saleOrAuctionLink;
    }

    public void increaseVoteCount(AuthenticityVoteChoice choice) {
        if (choice == AuthenticityVoteChoice.AUTHENTIC) {
            this.authenticVoteCount++;
            return;
        }
        this.fakeVoteCount++;
    }

    public void decreaseVoteCount(AuthenticityVoteChoice choice) {
        if (choice == AuthenticityVoteChoice.AUTHENTIC) {
            if (this.authenticVoteCount > 0) {
                this.authenticVoteCount--;
            }
            return;
        }
        if (this.fakeVoteCount > 0) {
            this.fakeVoteCount--;
        }
    }
}
