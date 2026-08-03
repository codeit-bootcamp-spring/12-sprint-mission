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
  //
  // 첫 페이지와 다음 페이지 쿼리를 분리한 이유
  // 1. (:cursor IS NULL OR ...) 형태는 cursor가 null일 때 PostgreSQL이 파라미터 타입을 추론하지 못해
  //    "could not determine data type of parameter" 오류가 발생한다
  // 2. OR 조건이 사라져서 (channel_id, created_at) 인덱스를 그대로 탈 수 있다
  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author "
      + "WHERE m.channel.id = :channelId "
      + "ORDER BY m.createdAt DESC")
  Slice<Message> findFirstPageByChannelId(
      @Param("channelId") UUID channelId,
      Pageable pageable
  );

  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author "
      + "WHERE m.channel.id = :channelId "
      + "AND m.createdAt < :cursor "
      + "ORDER BY m.createdAt DESC")
  Slice<Message> findNextPageByChannelId(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable
  );

  void deleteAllByChannel_Id(UUID channelId);
}
