package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  // ChannelMapper에서 단건 채널의 마지막 메시지 시각 조회 시 사용
  List<Message> findAllByChannel_Id(UUID channelId);

  // N+1 해결: 채널 목록의 마지막 메시지 시각을 한 번의 쿼리로 배치 조회
  @Query("SELECT m.channel.id, MAX(m.createdAt) FROM Message m "
      + "WHERE m.channel.id IN :channelIds GROUP BY m.channel.id")
  List<Object[]> findLastMessageAtByChannelIds(@Param("channelIds") List<UUID> channelIds);

  // 커서 기반 페이지네이션: author는 fetch join, attachments는 @BatchSize로 별도 조회
  // attachments를 fetch join하면 OneToMany + Pageable 조합으로 메모리 페이징 발생
  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author "
      + "WHERE m.channel.id = :channelId "
      + "AND (:cursor IS NULL OR m.createdAt < :cursor) "
      + "ORDER BY m.createdAt DESC")
  Slice<Message> findByChannelIdWithCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable
  );

  void deleteAllByChannel_Id(UUID channelId);
}
