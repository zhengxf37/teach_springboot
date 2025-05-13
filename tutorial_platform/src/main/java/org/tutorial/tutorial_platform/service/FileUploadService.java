package org.tutorial.tutorial_platform.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(Long userId, MultipartFile file);
}
