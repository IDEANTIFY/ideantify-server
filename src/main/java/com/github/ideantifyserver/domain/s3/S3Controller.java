package com.github.ideantifyserver.domain.s3;

import com.github.ideantifyserver.domain.s3.dto.UrlResponseDto;
import com.github.ideantifyserver.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "[S3]")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("/projects/image")
    public ApiResponse<UrlResponseDto> uploadImage(@RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(s3Service.uploadImage(file));
    }

    @PostMapping("/projects/file")
    public ApiResponse<UrlResponseDto> uploadFile(@RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(s3Service.uploadFile(file));
    }

    @PostMapping("/users/avatar")
    public ApiResponse<UrlResponseDto> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(s3Service.uploadAvatar(file));
    }
}
