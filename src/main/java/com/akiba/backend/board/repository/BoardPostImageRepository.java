package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardPostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPostImageRepository extends JpaRepository<BoardPostImage, Long> {

    List<BoardPostImage> findByPostIdOrderBySortOrderAscCreatedAtAsc(Long postId);

    List<BoardPostImage> findByPostIdInOrderByPostIdAscSortOrderAsc(List<Long> postIds);

    void deleteByPostId(Long postId);
}
