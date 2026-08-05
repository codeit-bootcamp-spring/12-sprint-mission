package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("""
      select distinct m
          from Message m
          join fetch m.channel c
          left join fetch m.author a
          left join fetch a.profile
          left join fetch m.attachments
          where m.id = :messageId
      """)
  Optional<Message> findDetailById(UUID messageId);

  @Query("""
      select m.channel.id, max(m.createdAt)
      from Message m
      where m.channel.id in :channelIds
      group by m.channel.id
      """)
  List<Object[]> findLastMessageAtByChannelIds(List<UUID> channelIds);

  @Query("""
      select distinct m
      from Message m
      join fetch m.channel c
      left join fetch m.author a
      left join fetch a.profile
      left join fetch m.attachments
      where m.id in :messageIds
      """)
  List<Message> findAllDetailByIdIn(List<UUID> messageIds);

  @Query("""
      select m.id
      from Message m
      where m.channel.id = :channelId
      """)
  Slice<UUID> findIdsByChannelId(UUID channelId, Pageable pageable);

  @Query("""
      select m.id
      from Message m
      where m.channel.id = :channelId
        and m.createdAt < :cursor
      """)
  Slice<UUID> findIdsByChannelIdAndCreatedAtLessThan(UUID channelId, Instant cursor,
      Pageable pageable
  );

  Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);
}
