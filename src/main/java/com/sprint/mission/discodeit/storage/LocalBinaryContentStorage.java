package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path rootPath;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.rootPath = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new BinaryContentStorageException("create storage directory: " + rootPath, e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try {
            Path path = resolvePath(id);
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new BinaryContentStorageException("store binary content: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            Path path = resolvePath(id);
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new BinaryContentStorageException("read binary content: " + id, e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponse binaryContent) {
        Resource resource = new InputStreamResource(get(binaryContent.id()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContent.contentType()))
                .contentLength(binaryContent.size())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + binaryContent.fileName() + "\""
                )
                .body(resource);
    }

    @Override
    public void delete(UUID id) {
        try {
            Path path = resolvePath(id);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new BinaryContentStorageException("delete binary content: " + id, e);
        }
    }

    private Path resolvePath(UUID id) {
        return rootPath.resolve(id.toString());
    }
}