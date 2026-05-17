package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path:.discodeit/storage}") String rootPath
    ) {
        this.root = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path filePath = resolvePath(id);
        try {
            Files.write(filePath, bytes);
            return id;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path filePath = resolvePath(id);
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        Resource resource = new InputStreamResource(get(binaryContentDto.id()));
        MediaType mediaType = binaryContentDto.contentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(binaryContentDto.contentType());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(binaryContentDto.size())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(binaryContentDto.fileName(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(resource);
    }

    private Path resolvePath(UUID id) {
        Path filePath = root.resolve(id.toString()).normalize();
        if (!filePath.startsWith(root)) {
            throw new IllegalArgumentException("Invalid file path: " + id);
        }
        return filePath;
    }
}
