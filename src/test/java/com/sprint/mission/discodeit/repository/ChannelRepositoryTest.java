package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
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
  private TestEntityManager em;

  private Channel createChannel(ChannelType type, String name) {
    Channel channel = new Channel(type, name, "description: " + name);
    return channelRepository.save(channel);
  }

  // ── findAllPublicOrId ───────────────────────────────────────────────────

  @Test
  @DisplayName("공개 채널이나 채널 리스트를 모두 조회")
  public void findAllPublicOrId() {
    Channel publicChannel1 = createChannel(ChannelType.PUBLIC, "publicChannel1");
    Channel publicChannel2 = createChannel(ChannelType.PUBLIC, "publicChannel2");
    Channel publicChannel3 = createChannel(ChannelType.PUBLIC, "publicChannel3");
    Channel privateChannel1 = createChannel(ChannelType.PRIVATE, "privateChannel1");
    Channel privateChannel2 = createChannel(ChannelType.PRIVATE, "privateChannel2");

    em.flush();
    em.clear();

    List<UUID> channelIds = List.of(privateChannel2.getId());
    List<Channel> channels = channelRepository.findAllPublicOrId(channelIds);

    assertThat(channels).hasSize(4);
    assertThat(channels).extracting("id")
        .containsExactlyInAnyOrder(publicChannel1.getId(), publicChannel2.getId(),
            publicChannel3.getId(), privateChannel2.getId());
    assertThat(channels).extracting("id")
        .doesNotContain(privateChannel1.getId());
    assertThat(channels).filteredOn(c -> c.getType() == ChannelType.PUBLIC).hasSize(3);
    assertThat(channels).filteredOn(c -> c.getType() == ChannelType.PRIVATE).hasSize(1);
  }

  @Test
  @DisplayName("PRIVATE 채널만 있고 ID 리스트가 비어있으면 빈 리스트 반환")
  public void findAllPublicOrId_emptyResult() {
    Channel privateChannel1 = createChannel(ChannelType.PRIVATE, "privateChannel1");
    Channel privateChannel2 = createChannel(ChannelType.PRIVATE, "privateChannel2");

    em.flush();
    em.clear();

    List<Channel> channel = channelRepository.findAllPublicOrId(List.of());

    assertThat(channel).isEmpty();
  }
}
