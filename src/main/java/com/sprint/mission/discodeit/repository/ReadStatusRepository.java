package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.repository.queryDsl.ReadStatusQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID>, ReadStatusQueryRepository {

  List<ReadStatus> findAllByUserId(UUID userId);

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
