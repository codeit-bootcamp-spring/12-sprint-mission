package com.sprint.mission.discodeit.event;

import java.util.UUID;

// BinaryContent 메타 데이터가 DB에 저장되었음을 의미하는 이벤트.
// 첨부 파일 1건당 1개를 발행해 파일 단위로 성공/실패를 격리한다.
public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

}
