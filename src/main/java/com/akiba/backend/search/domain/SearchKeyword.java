package com.akiba.backend.search.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String keyword;

    @Column(nullable = false)
    private int searchCount;

    private LocalDateTime lastSearchedAt;

    @PrePersist
    void prePersist() {
        if (this.searchCount <= 0) {
            this.searchCount = 1;
        }
        this.lastSearchedAt = LocalDateTime.now();
    }

    public void increment() {
        this.searchCount++;
        this.lastSearchedAt = LocalDateTime.now();
    }
}
