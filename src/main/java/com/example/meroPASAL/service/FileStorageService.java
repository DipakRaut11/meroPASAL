package com.example.meroPASAL.service;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(
            @Value("${app.uploads.payment-dir:uploads/payments}") String baseDir) throws IOException {
        this.root = Paths.get(baseDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String storePayment(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "payment" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);
        String filename = Instant.now().toEpochMilli() + "_" + UUID.randomUUID() + ext;
        Path target = root.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString(); // store absolute path (or make it relative by returning root.relativize(target).toString())
    }

}
