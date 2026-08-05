package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  @Query("""
      select rs
      from ReadStatus rs
      left join fetch rs.channel
      where rs.user.id= :userId
      """)
  List<ReadStatus> findAllByUserId(UUID userId);

  @Query("""
      select rs
      from ReadStatus rs
      join fetch rs.user u
      left join fetch u.profile
      where rs.channel.id = :channelId
      """)
  List<ReadStatus> findAllByChannelId(UUID channelId);

  boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
