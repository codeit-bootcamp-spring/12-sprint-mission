package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ReadStatusRepositoryTest {

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private TestEntityManager em;

  private User user1;
  private User user2;
  private Channel channel1;
  private Channel channel2;
  private ReadStatus readStatus1;
  private ReadStatus readStatus2;

  @BeforeEach
  void setUp() {
    user1 = new User("user1", "user1@example.com", "password1", null);
    em.persist(user1);

    user2 = new User("user2", "user2@example.com", "password2", null);
    em.persist(user2);

    channel1 = new Channel(ChannelType.PUBLIC, "채널1", "첫번째 채널");
    em.persist(channel1);

    channel2 = new Channel(ChannelType.PUBLIC, "채널2", "두번째 채널");
    em.persist(channel2);

    readStatus1 = new ReadStatus(user1, channel1, Instant.now());
    em.persist(readStatus1);

    readStatus2 = new ReadStatus(user1, channel2, Instant.now());
    em.persist(readStatus2);

    em.flush();
    em.clear();
  }

  // ── findAllByUserId ──────────────────────────────────────

  @Test
  @DisplayName("userId로 ReadStatus 목록 조회 성공")
  void findAllByUserId_Success() {
    // when
    List<ReadStatus> result = readStatusRepository.findAllByUserId(user1.getId());

    // then
    assertThat(result).hasSize(2);
    assertThat(result).allMatch(rs -> rs.getUser().getId().equals(user1.getId()));
  }

  @Test
  @DisplayName("ReadStatus 없는 userId 조회 시 빈 목록 반환")
  void findAllByUserId_NoResult_ReturnsEmpty() {
    // when
    List<ReadStatus> result = readStatusRepository.findAllByUserId(user2.getId());

    // then
    assertThat(result).isEmpty();
  }

  // ── findByUserIdAndChannelId ─────────────────────────────

  @Test
  @DisplayName("userId와 channelId로 ReadStatus 단건 조회 성공")
  void findByUserIdAndChannelId_Success() {
    // when
    Optional<ReadStatus> result = readStatusRepository.findByUserIdAndChannelId(
        user1.getId(), channel1.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getChannel().getId()).isEqualTo(channel1.getId());
  }

  @Test
  @DisplayName("존재하지 않는 조합으로 조회 시 empty 반환")
  void findByUserIdAndChannelId_NotFound_ReturnsEmpty() {
    // when
    Optional<ReadStatus> result = readStatusRepository.findByUserIdAndChannelId(
        user2.getId(), channel1.getId());

    // then
    assertThat(result).isEmpty();
  }

  // ── findAllByChannelIdWithUser ───────────────────────────

  @Test
  @DisplayName("channelId로 ReadStatus 목록 조회 성공")
  void findAllByChannelIdWithUser_Success() {
    // given
    ReadStatus readStatus3 = new ReadStatus(user2, channel1, Instant.now());
    em.persist(readStatus3);
    em.flush();
    em.clear();

    // when
    List<ReadStatus> result = readStatusRepository.findAllByChannelIdWithUser(channel1.getId());

    // then
    assertThat(result).hasSize(2);
    assertThat(result).allMatch(rs -> rs.getChannel().getId().equals(channel1.getId()));
  }

  @Test
  @DisplayName("ReadStatus 없는 channelId 조회 시 빈 목록 반환")
  void findAllByChannelIdWithUser_NoResult_ReturnsEmpty() {
    // given
    Channel emptyChannel = new Channel(ChannelType.PUBLIC, "빈채널", null);
    em.persist(emptyChannel);
    em.flush();
    em.clear();

    // when
    List<ReadStatus> result = readStatusRepository.findAllByChannelIdWithUser(emptyChannel.getId());

    // then
    assertThat(result).isEmpty();
  }

  // ── deleteAllByChannelId ─────────────────────────────────

  @Test
  @DisplayName("channelId로 ReadStatus 전체 삭제 성공")
  void deleteAllByChannelId_Success() {
    // when
    readStatusRepository.deleteAllByChannelId(channel1.getId());
    em.flush();
    em.clear();

    // then
    List<ReadStatus> result = readStatusRepository.findAllByUserId(user1.getId());
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getChannel().getId()).isEqualTo(channel2.getId());
  }
}