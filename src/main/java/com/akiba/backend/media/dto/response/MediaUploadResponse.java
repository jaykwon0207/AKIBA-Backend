package com.akiba.backend.media.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaUploadResponse {
    private Long mediaId;
    private String url;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
}
