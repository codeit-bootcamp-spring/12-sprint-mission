package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private S3Presigner s3Presigner;

  private S3BinaryContentStorage storage;

  private UUID testId;
  private byte[] testData;

  @BeforeEach
  void setUp() {
    testId = UuidCreator.getTimeOrderedEpoch();
    testData = "S3BinaryContentStorage 테스트".getBytes();

    storage = new S3BinaryContentStorage("fakeKey", "fakeSecret", "ap-northeast-2", "test-bucket");
    ReflectionTestUtils.setField(storage, "s3Client", s3Client);
    ReflectionTestUtils.setField(storage, "s3Presigner", s3Presigner);
    ReflectionTestUtils.setField(storage, "presignedUrlExpiration", 600L);
  }

  @Test
  @DisplayName("파일 업로드 성공 시 UUID 반환")
  void put() {
    // given
    given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .willReturn(PutObjectResponse.builder().build());

    // when
    UUID result = storage.put(testId, testData);

    // then
    assertThat(result).isEqualTo(testId);
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("업로드 중 S3 오류 발생 시 RuntimeException으로 변환")
  void put_RuntimeException() {
    // given
    given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .willThrow(mock(S3Exception.class));

    // when & then
    assertThatThrownBy(() -> storage.put(testId, testData))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("파일 다운로드 성공 시 InputStream 반환")
  void get() {
    // given
    ResponseInputStream<GetObjectResponse> mockStream = mock(ResponseInputStream.class);
    given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(mockStream);

    // when
    InputStream result = storage.get(testId);

    // then
    assertThat(result).isEqualTo(mockStream);
  }

  @Test
  @DisplayName("다운로드 중 S3 오류 발생 시 NoSuchElementException으로 변환")
  void get_NoSuchElementException() {
    // given
    given(s3Client.getObject(any(GetObjectRequest.class)))
        .willThrow(mock(S3Exception.class));

    // when & then
    assertThatThrownBy(() -> storage.get(testId))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  @DisplayName("download 호출 시 Presigned URL로 302 리다이렉트 반환")
  void download() throws MalformedURLException {
    // given
    BinaryContentDto dto = new BinaryContentDto(testId, "test.png", 100L, "image/png");
    String expectedUrl = "https://s3.amazonaws.com/test-bucket/" + testId;

    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    given(presignedRequest.url()).willReturn(new URL(expectedUrl));
    given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .willReturn(presignedRequest);

    // when
    ResponseEntity<Void> result = storage.download(dto);

    // then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(result.getHeaders().getLocation()).hasToString(expectedUrl);
  }

  @Test
  @DisplayName("Presigned URL 생성 중 S3 오류 발생 시 RuntimeException으로 변환")
  void download_RuntimeException() {
    // given
    BinaryContentDto dto = new BinaryContentDto(testId, "test.png", 100L, "image/png");
    given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .willThrow(mock(S3Exception.class));

    // when & then
    assertThatThrownBy(() -> storage.download(dto))
        .isInstanceOf(RuntimeException.class);
  }
}
