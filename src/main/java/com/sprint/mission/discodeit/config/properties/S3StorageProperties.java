package com.sprint.mission.discodeit.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
public record S3StorageProperties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    long presignedUrlExpiration
) {

}
