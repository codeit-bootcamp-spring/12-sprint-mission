package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Disabled
class AWSS3Test {

    static String accessKey;
    static String secretKey;
    static String region;
    static String bucket;

    // .env 파일을 Properties로 로드
    @BeforeAll
    static void loadEnv() throws IOException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(".env")) {
            props.load(is);
        }
        accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        region = props.getProperty("AWS_S3_REGION");
        bucket = props.getProperty("AWS_S3_BUCKET");
    }

    // 자격증명 + 리전으로 S3Client 생성
    private S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Test
    void upload() {
        byte[] content = "Hello S3!".getBytes(StandardCharsets.UTF_8);
        try (S3Client s3 = s3Client()) {
            s3.putObject(
                    PutObjectRequest.builder().bucket(bucket).key("test-key.txt").build(),
                    RequestBody.fromBytes(content));
        }
        System.out.println("✅ 업로드 성공: test-key.txt");
    }

    @Test
    void download() throws IOException {
        byte[] content = "Hello S3!".getBytes(StandardCharsets.UTF_8);
        try (S3Client s3 = s3Client()) {
            // 다운로드 검증을 위해 먼저 올림
            s3.putObject(
                    PutObjectRequest.builder().bucket(bucket).key("test-key.txt").build(),
                    RequestBody.fromBytes(content));

            ResponseInputStream<GetObjectResponse> obj = s3.getObject(
                    GetObjectRequest.builder().bucket(bucket).key("test-key.txt").build());
            byte[] downloaded = obj.readAllBytes();

            assertArrayEquals(content, downloaded);
            System.out.println("✅ 다운로드 성공: " + new String(downloaded, StandardCharsets.UTF_8));
        }
    }

    @Test
    void presignedUrl() {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build()) {

            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(bucket).key("test-key.txt").build();

            GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))   // 10분 유효
                    .getObjectRequest(getReq)
                    .build();

            PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
            URL url = presigned.url();

            assertNotNull(url);
            System.out.println("✅ Presigned URL (브라우저에 붙여넣어 확인):\n" + url);
        }
    }
}