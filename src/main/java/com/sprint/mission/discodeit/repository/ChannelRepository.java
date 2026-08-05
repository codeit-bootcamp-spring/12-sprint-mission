package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  //  현재 프로젝트에서는 public만 추가 조회가 필요하니까 하드코딩.
  @Query("""
      select distinct c
      from Channel c
      left join fetch c.readStatuses rs
      left join fetch rs.user u
      left join fetch u.profile
      where c.type = "PUBLIC"
        or c.id in(
                select rs2.channel.id
                from ReadStatus rs2
                where rs2.user.id = :userId
              )
      order by c.createdAt asc
      """)
  List<Channel> findAllByUserIdAndPublicChannelsWithParticipants(UUID userId);

  boolean existsByName(String name);
}
