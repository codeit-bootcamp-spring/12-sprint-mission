package com.sprint.mission.discodeit.exception.storage;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 업로드된 파일의 본문을 읽지 못했을 때 발생한다.
 *
 * <p>잘린 멀티파트처럼 대부분 클라이언트 입력 문제라 4xx로 응답한다. RuntimeException으로 감싸면
 * 서버 오류(500)로 집계되어 알람만 시끄러워진다.
 */
public class InvalidFileUploadException extends DiscodeitException {

  public InvalidFileUploadException(String fileName, Throwable cause) {
    super(ErrorCode.INVALID_FILE_UPLOAD,
        Map.of("fileName", fileName == null ? "" : fileName), cause);
  }
}
