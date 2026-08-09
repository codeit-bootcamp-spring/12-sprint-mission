package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUser_Id(UUID userId);

  List<ReadStatus> findAllByChannel_Id(UUID channelId);

  // N+1 해결: 채널 목록 조회 시 readStatus의 user(+profile)를 한 번에 로딩
  @EntityGraph(attributePaths = {"user", "user.profile"})
  List<ReadStatus> findAllByChannel_IdIn(List<UUID> channelIds);

  Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);

  void deleteAllByChannel_Id(UUID channelId);
}
