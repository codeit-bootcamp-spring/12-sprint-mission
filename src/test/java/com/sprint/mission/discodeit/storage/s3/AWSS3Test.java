package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSS3Test {

  private static S3Client s3Client;
  private static S3Presigner s3Presigner;
  private static String bucket;

  private static String fileName;
  private static String key;

  @BeforeAll
  public static void init() throws IOException {
    Properties properties = new Properties();

    properties.load(new FileInputStream(".env"));

    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        properties.getProperty("AWS_S3_ACCESS_KEY"),
        properties.getProperty("AWS_S3_SECRET_KEY")
    );

    bucket = properties.getProperty("AWS_S3_BUCKET");
    String region = properties.getProperty("AWS_S3_REGION");

    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    UUID binaryContentId = UUID.randomUUID();
    fileName = "test_" + binaryContentId;
    key = "attachments_test/" + binaryContentId;
  }

  @Test
  @Order(1)
  public void upload() {
    byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);

    PutObjectRequest putReq = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    PutObjectResponse response = s3Client.putObject(putReq, RequestBody.fromBytes(bytes));

    assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
  }

  @Test
  @Order(2)
  public void download() throws IOException {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getReq);
    String content = new String(response.readAllBytes());

    assertThat(content).isEqualTo(fileName);
  }

  @Test
  @Order(3)
  public void getUrl() {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getReq)
        .build();

    String url = s3Presigner.presignGetObject(presignReq).url().toExternalForm();

    assertThat(url).contains(bucket);
    assertThat(url).contains(key);
    System.out.println(url);
  }

  @AfterAll
  public static void close() {
    DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.deleteObject(deleteReq);
//    close
  }
}
