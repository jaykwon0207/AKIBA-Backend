package com.akiba.backend.media.repository;

import com.akiba.backend.media.domain.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
}
