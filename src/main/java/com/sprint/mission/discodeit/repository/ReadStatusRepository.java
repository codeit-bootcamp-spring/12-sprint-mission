package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @EntityGraph(attributePaths = {"channel"})
    List<ReadStatus> findAllByUser_Id(UUID userId);

    @EntityGraph(attributePaths = {"user", "user.profile", "user.userStatus"})
    List<ReadStatus> findAllByChannel_Id(UUID channelId);

    @EntityGraph(attributePaths = {"channel", "user", "user.profile", "user.userStatus"})
    List<ReadStatus> findAllByChannel_IdIn(Collection<UUID> channelIds);

    Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    void deleteAllByChannel_Id(UUID channelId);

    void deleteAllByUser_Id(UUID userId);

}
