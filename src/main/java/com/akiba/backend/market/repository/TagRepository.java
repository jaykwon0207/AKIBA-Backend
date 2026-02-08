package com.akiba.backend.market.repository;

import com.akiba.backend.market.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {}

