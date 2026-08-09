package com.sprint.mission.discodeit.repository.queryDsl;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusQueryRepository {

    List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);
}
