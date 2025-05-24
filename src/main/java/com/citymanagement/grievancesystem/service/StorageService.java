package com.citymanagement.grievancesystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    private final Path rootLocation = Paths.get("uploads");

    // Initialize storage directory
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    // Store single file and return stored file path string
    public String storeFile(MultipartFile file, String subFolder) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + file.getOriginalFilename());
            }

            Path dir = (subFolder == null || subFolder.isEmpty())
                    ? rootLocation
                    : rootLocation.resolve(subFolder);

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String filename = Path.of(file.getOriginalFilename()).getFileName().toString();
            Path destinationFile = dir.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path from uploads folder (e.g., "123/filename.jpg")
            return (subFolder == null || subFolder.isEmpty())
                    ? filename
                    : subFolder + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    // Store multiple files and return list of stored paths
    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> storedPaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String storedPath = storeFile(file, null);  // or pass subFolder if you want to group by complaint ID
            storedPaths.add(storedPath);
        }
        return storedPaths;
    }

    // Delete file by subfolder and filename
    public void deleteFile(String subFolder, String filename) {
        try {
            Path filePath = (subFolder == null || subFolder.isEmpty())
                    ? rootLocation.resolve(filename)
                    : rootLocation.resolve(subFolder).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + filename, e);
        }
    }
}
