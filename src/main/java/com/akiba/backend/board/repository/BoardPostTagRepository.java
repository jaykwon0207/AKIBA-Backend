package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardPostTag;
import com.akiba.backend.board.domain.BoardPostTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPostTagRepository extends JpaRepository<BoardPostTag, BoardPostTagId> {

    List<BoardPostTag> findByPostId(Long postId);

    List<BoardPostTag> findByPostIdIn(List<Long> postIds);

    List<BoardPostTag> findByTagId(Long tagId);

    void deleteByPostId(Long postId);
}
