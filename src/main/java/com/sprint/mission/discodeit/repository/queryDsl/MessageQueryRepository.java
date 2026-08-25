package com.sprint.mission.discodeit.repository.queryDsl;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageQueryRepository {

    Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
                                                @Param("createdAt") Instant createdAt,
                                                Pageable pageable);

    Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

}
