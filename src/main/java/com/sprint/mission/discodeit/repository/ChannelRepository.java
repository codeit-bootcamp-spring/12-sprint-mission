package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  // PUBLIC 채널 또는 사용자가 참여 중인 PRIVATE 채널을 한 번의 쿼리로 조회
  @Query("SELECT c FROM Channel c WHERE c.type = :publicType OR c.id IN :channelIds")
  List<Channel> findPublicOrIn(
      @Param("publicType") ChannelType publicType,
      @Param("channelIds") List<UUID> channelIds
  );
}
