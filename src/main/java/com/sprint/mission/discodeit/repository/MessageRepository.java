package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByAuthorId(UUID authorId);

  @EntityGraph(attributePaths = {"channel", "author"})
  List<Message> findTop50ByChannelIdOrderByCreatedAtDesc(UUID channelId);

  @EntityGraph(attributePaths = {"channel", "author"})
  List<Message> findTop50ByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId,
      Instant createdAt);

  Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}