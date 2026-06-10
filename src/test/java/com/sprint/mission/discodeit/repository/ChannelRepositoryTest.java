package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
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
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager em;

  private Channel publicChannel1;
  private Channel publicChannel2;
  private Channel privateChannel;

  @BeforeEach
  void setUp() {
    publicChannel1 = new Channel(ChannelType.PUBLIC, "공개채널1", "공개 채널입니다");
    em.persist(publicChannel1);

    publicChannel2 = new Channel(ChannelType.PUBLIC, "공개채널2", "공개 채널입니다");
    em.persist(publicChannel2);

    privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    em.persist(privateChannel);

    em.flush();
    em.clear();
  }

  // ── findAllByTypeOrIdIn ──────────────────────────────────

  @Test
  @DisplayName("PUBLIC 타입 채널 전체 조회 성공")
  void findAllByTypeOrIdIn_PublicChannels_Success() {
    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of()
    );

    // then
    assertThat(result).hasSize(2);
    assertThat(result).allMatch(c -> c.getType().equals(ChannelType.PUBLIC));
  }

  @Test
  @DisplayName("PRIVATE 채널 ID로 조회 성공")
  void findAllByTypeOrIdIn_WithPrivateChannelId_Success() {
    // given
    List<UUID> privateChannelIds = List.of(privateChannel.getId());

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        privateChannelIds
    );

    // then
    assertThat(result).hasSize(3);
    assertThat(result).anyMatch(c -> c.getType().equals(ChannelType.PRIVATE));
  }

  @Test
  @DisplayName("존재하지 않는 ID로 조회 시 PUBLIC 채널만 반환")
  void findAllByTypeOrIdIn_WithNonExistentId_ReturnsOnlyPublic() {
    // given
    List<UUID> nonExistentIds = List.of(UUID.randomUUID());

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        nonExistentIds
    );

    // then
    assertThat(result).hasSize(2);
    assertThat(result).allMatch(c -> c.getType().equals(ChannelType.PUBLIC));
  }

  @Test
  @DisplayName("채널 없으면 빈 목록 반환")
  void findAllByTypeOrIdIn_NoChannels_ReturnsEmpty() {
    // given
    channelRepository.deleteAll();
    em.flush();
    em.clear();

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of()
    );

    // then
    assertThat(result).isEmpty();
  }
}