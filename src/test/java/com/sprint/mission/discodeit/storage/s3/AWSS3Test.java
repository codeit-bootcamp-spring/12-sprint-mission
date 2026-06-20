package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
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
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Disabled
public class AWSS3Test {

  static final String TEST_KEY = "test/hello.txt";
  private static String accessKey;
  private static String secretKey;
  private static String region;
  private static String bucket;
  private static S3Client s3Client;
  private static S3Presigner s3Presigner;

  @BeforeAll
  static void init() throws IOException {
    Properties props = new Properties();
    props.load(Files.newInputStream(Path.of(".env")));

    accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    region = props.getProperty("AWS_S3_REGION");
    bucket = props.getProperty("AWS_S3_BUCKET");

    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    StaticCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();

    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }

  @Test
  @Order(1)
  void upload() {
    PutObjectRequest putReq = PutObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .contentType("text/plain")
        .build();
    PutObjectResponse response = s3Client.putObject(putReq,
        RequestBody.fromBytes("Hello, S3!".getBytes()));
    assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
    System.out.println("업로드 완료: " + TEST_KEY);
  }

  @Test
  @Order(2)
  void download() throws IOException {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();

    ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getReq);
    String content = new String(response.readAllBytes());

    assertThat(content).isEqualTo("Hello, S3!");
    System.out.println("다운로드 완료 - 내용: " + content);
  }

  @Test
  @Order(3)
  void presignedUrl() {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(TEST_KEY)
        .build();

    GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getReq)
        .build();

    String url = s3Presigner.presignGetObject(presignReq).url().toExternalForm();

    assertThat(url).startsWith("https://");
    assertThat(url).contains(bucket);
    System.out.println("PresignedUrl: " + url);
  }
}
