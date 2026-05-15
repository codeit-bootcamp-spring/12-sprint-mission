package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("SELECT c FROM Channel c "
      + "WHERE c.type = 'PUBLIC' OR c.id IN :channelIds")
  List<Channel> findAllPublicOrId(List<UUID> channelIds);
}
