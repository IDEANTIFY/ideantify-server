package com.github.ideantifyserver.domain.s3;

import com.github.ideantifyserver.domain.s3.dto.UrlResponseDto;
import com.github.ideantifyserver.domain.s3.exception.S3Exceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif", "image/webp", "image/svg+xml"
    );
    private static final Set<String> FILE_EXT_WHITELIST = Set.of(
            "pdf","doc","docx","ppt","pptx","xls","xlsx","csv","zip","rar","7z","txt","md"
    );

    public UrlResponseDto uploadImage(MultipartFile file) {
        String ct = file.getContentType();
        if (ct == null || !IMAGE_CONTENT_TYPES.contains(ct.toLowerCase())) {
            throw S3Exceptions.INVALID_CONTENT_TYPE.toException();
        }

        String filename = sanitize(file.getOriginalFilename());
        String key = "projects/images/%s_%s".formatted(UUID.randomUUID(), filename);

        try (InputStream in = file.getInputStream()) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(ct)
                            .build(),
                    RequestBody.fromInputStream(in, file.getSize())
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw S3Exceptions.UPLOAD_FAIL.toException();
        }

        return UrlResponseDto.of(publicUrl(bucket, region, key));
    }

    public UrlResponseDto uploadFile(MultipartFile file) {
        String filename = sanitize(file.getOriginalFilename());
        String ext = getExt(filename);
        if (ext == null || !FILE_EXT_WHITELIST.contains(ext.toLowerCase())) {
            throw S3Exceptions.INVALID_CONTENT_TYPE.toException();
        }

        String key = "projects/files/%s_%s".formatted(UUID.randomUUID(), filename);
        String ct = file.getContentType();
        if (ct == null) ct = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        try (InputStream in = file.getInputStream()) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(ct)
                            .build(),
                    RequestBody.fromInputStream(in, file.getSize())
            );
        } catch (Exception e) {
            throw S3Exceptions.UPLOAD_FAIL.toException();
        }

        return UrlResponseDto.of(publicUrl(bucket, region, key));
    }

    private String publicUrl(String bucket, String region, String key) {
        String encodedPath = java.util.Arrays.stream(key.split("/"))
                .map(seg -> java.net.URLEncoder.encode(seg, java.nio.charset.StandardCharsets.UTF_8)
                        .replace("+", "%20"))
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedPath;
    }

    private String sanitize(String filename) {
        String name = (filename == null || filename.isBlank()) ? "file" : filename;
        name = name.replaceAll("[\\\\/\\s]+", "_");
        return name.length() > 180 ? name.substring(name.length() - 180) : name;
    }

    private String getExt(String filename) {
        int i = filename.lastIndexOf('.');
        return (i > -1 && i < filename.length() - 1) ? filename.substring(i + 1) : null;
    }

    public UrlResponseDto uploadAvatar(MultipartFile file) {
        String ct = file.getContentType();
        if (ct == null || !IMAGE_CONTENT_TYPES.contains(ct.toLowerCase())) {
            throw S3Exceptions.INVALID_CONTENT_TYPE.toException();
        }

        String filename = sanitize(file.getOriginalFilename());
        String key = "users/avatars/%s_%s".formatted(UUID.randomUUID(), filename);

        try (InputStream in = file.getInputStream()) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(ct)
                            .build(),
                    RequestBody.fromInputStream(in, file.getSize())
            );
        } catch (Exception e) {
            log.error("Failed to upload avatar: {}", e.getMessage(), e);
            throw S3Exceptions.UPLOAD_FAIL.toException();
        }

        return UrlResponseDto.of(publicUrl(bucket, region, key));
    }
}
