package com.sprint.mission.discodeit.entity;

// 메타 데이터(DB)와 별개로 진행되는 바이너리 데이터 업로드의 결과를 표현한다
public enum BinaryContentStatus {
  // 메타 데이터만 저장된 상태 - 바이너리 업로드는 커밋 이후 진행된다
  PROCESSING,
  SUCCESS,
  FAIL
}
