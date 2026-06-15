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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  private Channel publicChannel;
  private Channel privateChannel;

  @BeforeEach
  void setUp() {
    publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", null));
    privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));
  }

  @Test
  @DisplayName("PUBLIC 채널 + 구독한 PRIVATE 채널 조회")
  void findAllByTypeOrIdIn_publicAndPrivate() {
    // given
    List<UUID> subscribedIds = List.of(privateChannel.getId());

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, subscribedIds);

    // then
    assertThat(result).hasSize(2);
    assertThat(result).extracting(Channel::getId)
        .containsExactlyInAnyOrder(publicChannel.getId(), privateChannel.getId());
  }

  @Test
  @DisplayName("구독 채널 없을 때 PUBLIC만 반환")
  void findAllByTypeOrIdIn_onlyPublic() {
    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  @DisplayName("PUBLIC 채널 없을 때 구독 채널만 반환")
  void findAllByTypeOrIdIn_onlyPrivate() {
    channelRepository.delete(publicChannel);

    Channel onlyPrivate = channelRepository.save(
        new Channel(ChannelType.PRIVATE, null, null)
    );
    List<UUID> subscribedIds = List.of(onlyPrivate.getId());

    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC, subscribedIds
    );

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType()).isEqualTo(ChannelType.PRIVATE);
  }

  @Test
  @DisplayName("PUBLIC 채널도 없고 구독 채널도 없을 때 빈 리스트 반환")
  void findAllByTypeOrIdIn_empty() {
    // given
    channelRepository.delete(publicChannel);
    channelRepository.delete(privateChannel);

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

    // then
    assertThat(result).isEmpty();
  }
}