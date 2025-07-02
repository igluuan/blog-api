package com.devluan.blog_api.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final List<String> allowedImageTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
    private final long maxFileSize = 5 * 1024 * 1024; // 5MB

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        if (!allowedImageTypes.contains(file.getContentType())) {
            throw new IOException("Invalid file type. Only JPEG, PNG, GIF are allowed.");
        }

        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds the maximum limit of 5MB.");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return "/uploads/" + fileName; // Return a relative URL
    }
}