package com.rvinproject.camerarentalbe.app.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    private static final long MAX_SIZE = 3L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp", "avif");
    private final Path storageRoot = Paths.get("storage").toAbsolutePath().normalize();

    public String storeImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (file.getSize() >= MAX_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ukuran image maksimal kurang dari 3MB");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = extension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format image harus png, jpg, jpeg, webp, atau avif");
        }

        try {
            Path targetDirectory = storageRoot.resolve(folder).normalize();
            if (!targetDirectory.startsWith(storageRoot)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder storage tidak valid");
            }
            Files.createDirectories(targetDirectory);

            String filename = UUID.randomUUID() + "." + extension;
            Path target = targetDirectory.resolve(filename).normalize();
            file.transferTo(target);
            return "/storage/" + ("public".equals(folder) ? filename : folder + "/" + filename);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal menyimpan image");
        }
    }

    public Resource resource(String relativePath) {
        try {
            Path file = storageRoot.resolve(relativePath).normalize();
            if (!file.startsWith(storageRoot) || !Files.exists(file) || !Files.isRegularFile(file)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File tidak ditemukan");
            }
            return new UrlResource(file.toUri());
        } catch (MalformedURLException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File tidak ditemukan");
        }
    }

    public void deleteStoredFile(String storedPath) {
        if (!StringUtils.hasText(storedPath) || !storedPath.startsWith("/storage/")) {
            return;
        }

        Path file = storageRoot.resolve(storedPath.substring("/storage/".length())).normalize();
        if (!file.startsWith(storageRoot)) {
            return;
        }

        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            log.warn("Gagal menghapus file storage: {}", storedPath, exception);
        }
    }

    private String extension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image wajib memiliki ekstensi file");
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
