package com.sprint.mission.discodeit.exception.storage;

import java.util.UUID;

/**
 * 다시 시도하면 성공할 여지가 있는 저장소 실패.
 *
 * <p>네트워크 오류, 타임아웃, S3의 5xx / 429처럼 원인이 일시적인 경우만 여기에 해당한다.
 * 자격증명 오류나 없는 버킷처럼 몇 번을 시도해도 결과가 같은 실패는 {@link StorageException}으로
 * 남겨 재시도 없이 곧바로 실패 처리한다.
 */
public class RetryableStorageException extends StorageException {

  public RetryableStorageException(String operation, UUID id) {
    super(operation, id);
  }
}
