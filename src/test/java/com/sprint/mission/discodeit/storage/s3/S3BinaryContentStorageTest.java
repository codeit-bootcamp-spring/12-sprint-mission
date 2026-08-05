package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sprint.mission.discodeit.config.properties.S3StorageProperties;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.FileStorageException;
import com.sprint.mission.discodeit.storage.impl.S3BinaryContentStorage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

@SpringBootTest(properties = "discodeit.storage.type=s3")
@ActiveProfiles("test")
public class S3BinaryContentStorageTest {

  @Autowired
  private S3BinaryContentStorage binaryContentStorage;

  @Autowired
  private S3StorageProperties properties;

  @Autowired
  private S3Client s3Client;

  private UUID id;
  private byte[] bytes;
  private String contentType;

  @BeforeEach
  public void setup() {
    id = UUID.randomUUID();
    bytes = "test".getBytes(StandardCharsets.UTF_8);
    contentType = "text/plain";
  }

  @Test
  @DisplayName("save_and_get_success")
  void save_success() throws IOException {
    UUID binaryContentId = binaryContentStorage.put(id, bytes, contentType);

    assertThat(binaryContentId).isNotNull();
    assertThat(binaryContentId).isEqualTo(id);

    byte[] savedBytes = binaryContentStorage.get(id).readAllBytes();
    String savedContentType = s3Client.headObject(HeadObjectRequest.builder()
        .bucket(properties.bucket())
        .key("attachments/" + id)
        .build()).contentType();

    assertThat(savedBytes).isEqualTo(bytes);
    assertThat(savedContentType).isEqualTo(contentType);
  }

  @Test
  @DisplayName("save_failed")
  void save_failed() {
    assertThatThrownBy(() -> binaryContentStorage.put(id, null, contentType))
        .isInstanceOf(FileStorageException.class);
  }

  @Test
  @DisplayName("get_failed")
  void get_failed() {
    assertThatThrownBy(() -> binaryContentStorage.get(id))
        .isInstanceOf(FileStorageException.class);
  }

  @Test
  @DisplayName("save_and_download_success")
  void download() {
    UUID binaryContentId = binaryContentStorage.put(id, bytes, contentType);

    assertThat(binaryContentId).isNotNull();

    BinaryContentDto binaryContentDto = new BinaryContentDto(
        id,
        "test.txt",
        bytes.length,
        "text/plain"
    );

    ResponseEntity<Resource> response = binaryContentStorage.download(binaryContentDto);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(response.getHeaders().getLocation()).isNotNull();

    String location = Objects.requireNonNull(response.getHeaders().getLocation()).toString();

    assertThat(location).contains(id.toString());
  }

  @AfterEach
  public void cleanUp() {
    DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
        .bucket(properties.bucket())
        .key("attachments/" + id)
        .build();

    s3Client.deleteObject(deleteReq);
  }
}
