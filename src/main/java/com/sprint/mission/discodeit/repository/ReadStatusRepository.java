package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  Optional<ReadStatus> findByUserIdAndChannelId(UUID user_id, UUID channel_id);

  void deleteAllByChannelId(UUID channelId);

  @Query("SELECT r FROM ReadStatus r "
      + "JOIN FETCH r.user u "
      + "JOIN FETCH u.userStatus s "
      + "LEFT JOIN FETCH u.profile p "
      + "WHERE r.channel.id = :channelId")
  List<ReadStatus> findAllByChannelIdWithUser(UUID channelId);

  boolean existsAllByChannelIdAndUserId(UUID channelId, UUID authorId);
}
