package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

// 저장 매체를 추상화하는 인터페이스 - 로컬/원격 저장소 구현체 교체 가능
public interface BinaryContentStorage {

  // BinaryContent의 id를 키로 bytes 저장
  UUID put(UUID id, byte[] bytes);

  // id로 bytes를 읽어 InputStream 반환
  InputStream get(UUID id);

  // HTTP 다운로드 응답 생성 (Content-Type, Content-Disposition 포함)
  ResponseEntity<?> download(BinaryContentDto dto);
}
