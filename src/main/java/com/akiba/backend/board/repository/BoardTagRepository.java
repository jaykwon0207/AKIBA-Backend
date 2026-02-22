package com.akiba.backend.board.repository;

import com.akiba.backend.board.domain.BoardTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {

    Optional<BoardTag> findByNameIgnoreCase(String name);
}
