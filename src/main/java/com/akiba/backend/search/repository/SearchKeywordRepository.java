package com.akiba.backend.search.repository;

import com.akiba.backend.search.domain.SearchKeyword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    Optional<SearchKeyword> findByKeyword(String keyword);

    List<SearchKeyword> findAllByOrderBySearchCountDescLastSearchedAtDesc(Pageable pageable);

    long deleteByLastSearchedAtBefore(LocalDateTime cutoff);

    long deleteBySearchCountLessThanEqualAndLastSearchedAtBefore(int maxCount, LocalDateTime cutoff);
}
