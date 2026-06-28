package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.util.UUID;

@Component
@ConditionalOnProperty(
        name = "discodeit.storage.type",
        havingValue = "s3"
)
public class S3BinaryContentStorage implements BinaryContentStorage {

    private String accesskey;
    private String secretKey;
    private String region;
    private String bucket;

    public S3BinaryContentStorage(String accesskey, String secretKey, String region, String bucket) {}

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        return null;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        return null;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        return null;
    }

    private S3Client getS3Client() {
        return null;
    }

    private String generatePresignedUrl(String key, String ContentType){
        return null;
    }
}
