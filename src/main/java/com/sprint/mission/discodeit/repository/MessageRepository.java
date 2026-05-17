package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {
            "channel",
            "author",
            "author.profile",
            "author.userStatus"
    } )
    Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);

    Optional<Message> findFirstByChannel_IdOrderByCreatedAtDesc(UUID channelId);

    void deleteAllByChannel_Id(UUID channelId);

    @Query("""
            select m.channel.id as channelId, max(m.createdAt) as lastMessageAt
            from Message m
            where m.channel.id in :channelIds
            group by m.channel.id
    """)
    List<ChannelLastMessageAtProjection> findLastMessageAtsByChannelIds(
            @Param("channelIds") Collection<UUID> channelIds
            );

    interface ChannelLastMessageAtProjection {
        UUID getChannelId();
        Instant getLastMessageAt();
    }
}
