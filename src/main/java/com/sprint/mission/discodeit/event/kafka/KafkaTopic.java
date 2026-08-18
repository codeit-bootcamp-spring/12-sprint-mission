package com.sprint.mission.discodeit.event.kafka;

/**
 * 토픽 이름은 발행자와 소비자가 문자열로만 합의하므로, 오타 하나로 조용히 어긋난다.
 *
 * <p>{@code @KafkaListener(topics = ...)}는 컴파일 타임 상수만 받기 때문에 상수로 모아둔다.
 */
public final class KafkaTopic {

  public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
  public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
  public static final String S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";

  private KafkaTopic() {
  }
}
