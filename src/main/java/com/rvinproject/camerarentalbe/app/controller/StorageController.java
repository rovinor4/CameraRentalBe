package com.rvinproject.camerarentalbe.app.controller;

import com.rvinproject.camerarentalbe.app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class StorageController {
    private final FileStorageService fileStorageService;

    @GetMapping("/storage/{filename:.+}")
    public ResponseEntity<Resource> publicFile(@PathVariable String filename) throws IOException {
        return response(fileStorageService.resource("public/" + filename));
    }

    @GetMapping("/storage/payment-methods/{filename:.+}")
    public ResponseEntity<Resource> paymentMethodFile(@PathVariable String filename) throws IOException {
        return response(fileStorageService.resource("payment-methods/" + filename));
    }

    private ResponseEntity<Resource> response(Resource resource) throws IOException {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        String contentType = resource.getURL().openConnection().getContentType();
        if (contentType != null) {
            mediaType = MediaType.parseMediaType(contentType);
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .body(resource);
    }
}
