package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  private Channel createChannel(String name, String description) {
    return Channel.builder()
        .name(name)
        .description(description)
        .type(ChannelType.PUBLIC)
        .build();
  }

  @Test
  @DisplayName("findById_success")
  void findDetailById_success() {
    Channel createdChannel = channelRepository.save(
        createChannel("channel1", "channel1_description"));
    testEntityManager.flush();
    testEntityManager.clear();

    Optional<Channel> channel = channelRepository.findById(createdChannel.getId());

    assertThat(channel).isPresent();
    assertThat(channel.get().getName()).isEqualTo("channel1");
    assertThat(channel.get().getDescription()).isEqualTo("channel1_description");
  }

  @Test
  @DisplayName("findById_failed")
  void findDetailById_failed() {
    Optional<Channel> channel = channelRepository.findById(UUID.randomUUID());

    assertThat(channel).isEmpty();
  }

  @Test
  @DisplayName("findAllByUserIdAndPublicChannelsWithParticipants_success")
  void findAllByUserIdAndPublicChannelsWithParticipants_success() {
    channelRepository.save(createChannel("channel1", "channel1_description"));
    channelRepository.save(createChannel("channel2", "channel1_description"));
    testEntityManager.flush();
    testEntityManager.clear();

    List<Channel> channels = channelRepository
        .findAllByUserIdAndPublicChannelsWithParticipants(UUID.randomUUID());
    
    assertThat(channels)
        .extracting(Channel::getName)
        .containsExactlyInAnyOrder("channel1", "channel2");
  }

  @Test
  @DisplayName("findAllByUserIdAndPublicChannelsWithParticipants_failed")
  void findAllByUserIdAndPublicChannelsWithParticipants_failed() {
    List<Channel> channels = channelRepository
        .findAllByUserIdAndPublicChannelsWithParticipants(UUID.randomUUID());

    assertThat(channels.size()).isEqualTo(0);
  }
}
