package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

import com.sprint.mission.discodeit.storage.S3BinaryContentStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Disabled
class S3BinaryContentStorageTest {

    static S3BinaryContentStorage storage;

    @BeforeAll
    static void setUp() throws IOException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(".env")) {
            props.load(is);
        }
        storage = new S3BinaryContentStorage(
                props.getProperty("AWS_S3_ACCESS_KEY"),
                props.getProperty("AWS_S3_SECRET_KEY"),
                props.getProperty("AWS_S3_REGION"),
                props.getProperty("AWS_S3_BUCKET"),
                600
        );
    }

    @Test
    void put() {
        UUID id = UUID.randomUUID();
        byte[] data = "s3 storage test".getBytes(StandardCharsets.UTF_8);
        assertEquals(id, storage.put(id, data));
    }

    @Test
    void get() throws IOException {
        UUID id = UUID.randomUUID();
        byte[] data = "s3 storage test".getBytes(StandardCharsets.UTF_8);
        storage.put(id, data);
        try (InputStream is = storage.get(id)) {
            assertArrayEquals(data, is.readAllBytes());
        }
    }

    @Test
    void download() {
        UUID id = UUID.randomUUID();
        byte[] data = "download test".getBytes(StandardCharsets.UTF_8);
        storage.put(id, data);

        BinaryContentDto dto =
                new BinaryContentDto(id, "test.txt", (long) data.length, "text/plain",
                    BinaryContentStatus.SUCCESS, null);
        ResponseEntity<Void> response = storage.download(dto);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());        // 302
        assertNotNull(response.getHeaders().getLocation());              // presigned url
        System.out.println("Presigned URL: " + response.getHeaders().getLocation());
    }
}