package com.globeot.globeotback.global.controller;

import com.globeot.globeotback.application.service.S3Service;
import com.globeot.globeotback.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "Image", description = "이미지 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        String url = s3Service.upload(file);
        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("url", url)));
    }
}
