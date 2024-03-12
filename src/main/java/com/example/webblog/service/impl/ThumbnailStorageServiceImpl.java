package com.example.webblog.service.impl;

import com.example.webblog.controller.PostController;
import com.example.webblog.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Qualifier("thumbnailStorageService")
public class ThumbnailStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("uploads");
    private final Path thumbnail = root.resolve("thumbnail");

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.thumbnail.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = thumbnail.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public String buildFileUrl(MultipartFile file) {
        String url = MvcUriComponentsBuilder
                .fromMethodName(PostController.class, "getThumbnail", file.getOriginalFilename())
                .build()
                .toString();

        return url;
    }
}
