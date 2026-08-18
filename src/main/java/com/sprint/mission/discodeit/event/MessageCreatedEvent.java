package com.sprint.mission.discodeit.event;

import java.util.UUID;

// 6단계에서 이 이벤트를 그대로 JSON으로 직렬화해 Kafka로 보낸다.
// 엔티티를 담으면 지연 로딩 프록시와 순환 참조 때문에 직렬화가 깨지므로 값만 복사한다.
public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    String channelName,
    UUID authorId,
    String authorUsername,
    String content
) {

}
