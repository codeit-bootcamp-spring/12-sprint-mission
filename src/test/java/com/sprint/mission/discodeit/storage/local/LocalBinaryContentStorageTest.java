package com.sprint.mission.discodeit.storage.local;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.BinaryContentFileNotFoundException;
import com.sprint.mission.discodeit.exception.file.FileStorageException;
import com.sprint.mission.discodeit.storage.impl.LocalBinaryContentStorage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class LocalBinaryContentStorageTest {

  @TempDir
  private Path tempDir;

  private LocalBinaryContentStorage binaryContentStorage;
  private UUID id;
  private byte[] bytes;
  private String contentType;

  @BeforeEach
  void setUp() {
    binaryContentStorage = new LocalBinaryContentStorage(tempDir.toString());
    binaryContentStorage.init();
    id = UUID.randomUUID();
    bytes = "test".getBytes(StandardCharsets.UTF_8);
    contentType = "text/plain";
  }

  @Test
  @DisplayName("init_success")
  void init_success() {
    Path storageRoot = tempDir.resolve("test");
    LocalBinaryContentStorage storage = new LocalBinaryContentStorage(storageRoot.toString());

    storage.init();

    assertThat(storageRoot).exists().isDirectory();
  }

  @Test
  @DisplayName("init_failed_with_target_is_file")
  void init_failed_whenRootPathIsFile() throws IOException {
    Path filePath = tempDir.resolve("file_name");
    Files.writeString(filePath, "not a directory");

    LocalBinaryContentStorage storage = new LocalBinaryContentStorage(filePath.toString());

    assertThatThrownBy(storage::init)
        .isInstanceOf(FileStorageException.class);
  }

  @Test
  @DisplayName("put_and_get_success")
  void putAndGet_success() throws IOException {
    UUID binaryContentId = binaryContentStorage.put(id, bytes, contentType);

    assertThat(binaryContentId).isEqualTo(id);
    assertThat(binaryContentStorage.resolvePath(id)).exists();
    assertThat(binaryContentStorage.get(id).readAllBytes()).isEqualTo(bytes);
  }

  @Test
  @DisplayName("put_failed")
  void put_failed() throws IOException {
    Path directoryPath = binaryContentStorage.resolvePath(id);
    Files.createDirectories(directoryPath);

    assertThatThrownBy(() -> binaryContentStorage.put(id, bytes, contentType))
        .isInstanceOf(FileStorageException.class);
  }

  @Test
  @DisplayName("get_failed")
  void get_failed() {
    assertThatThrownBy(() -> binaryContentStorage.get(id))
        .isInstanceOf(BinaryContentFileNotFoundException.class);
  }

  @Test
  @DisplayName("download_success")
  void download_success() throws IOException {
    binaryContentStorage.put(id, bytes, contentType);
    BinaryContentDto binaryContentDto = new BinaryContentDto(
        id,
        "test.txt",
        bytes.length,
        "text/plain"
    );

    ResponseEntity<Resource> response = binaryContentStorage.download(binaryContentDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
    assertThat(response.getHeaders().getContentLength()).isEqualTo(bytes.length);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getInputStream().readAllBytes()).isEqualTo(bytes);

    ContentDisposition contentDisposition = response.getHeaders().getContentDisposition();
    assertThat(contentDisposition.getType()).isEqualTo("attachment");
    assertThat(contentDisposition.getFilename()).isEqualTo("test.txt");
  }

  @Test
  @DisplayName("delete_success")
  void delete_success() {
    binaryContentStorage.put(id, bytes, contentType);

    binaryContentStorage.delete(id);

    assertThat(Files.exists(binaryContentStorage.resolvePath(id))).isFalse();
  }

  @Test
  @DisplayName("delete_failed")
  void delete_failed() throws IOException {
    Path directoryPath = binaryContentStorage.resolvePath(id);
    Files.createDirectories(directoryPath);
    Files.writeString(directoryPath.resolve("under_directory.txt"), "test");

    assertThatThrownBy(() -> binaryContentStorage.delete(id))
        .isInstanceOf(FileStorageException.class);
  }
}
