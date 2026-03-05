package com.akiba.backend.media.service;

import com.akiba.backend.media.domain.MediaFile;
import com.akiba.backend.media.dto.response.MediaUploadResponse;
import com.akiba.backend.media.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaService {

    private final MediaFileRepository mediaFileRepository;

    @Value("${app.media.upload-dir:/tmp/akiba-uploads}")
    private String uploadDir;

    @Transactional
    public MediaUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다.");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = extractExtension(originalName);
        String storedName = UUID.randomUUID() + extension;
        Path directory = Paths.get(uploadDir);
        Path target = directory.resolve(storedName).normalize();

        try {
            Files.createDirectories(directory);
            file.transferTo(target);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.");
        }

        MediaFile mediaFile = MediaFile.builder()
                .url("")
                .storagePath(target.toString())
                .originalFilename(originalName)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        MediaFile saved = mediaFileRepository.save(mediaFile);
        saved.updateUrl("/api/media/files/" + saved.getMediaId());
        saved = mediaFileRepository.save(saved);

        return MediaUploadResponse.builder()
                .mediaId(saved.getMediaId())
                .url(saved.getUrl())
                .originalFilename(saved.getOriginalFilename())
                .contentType(saved.getContentType())
                .fileSize(saved.getFileSize())
                .build();
    }

    public Resource loadAsResource(Long mediaId) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다."));
        try {
            Resource resource = new UrlResource(Paths.get(mediaFile.getStoragePath()).toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 경로가 올바르지 않습니다.");
        }
    }

    public MediaFile getMedia(Long mediaId) {
        return mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다."));
    }

    private String extractExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) return "";
        return filename.substring(lastDot);
    }
}
