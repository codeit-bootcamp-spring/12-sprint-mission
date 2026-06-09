package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("공개 채널과 참여 중인 비공개 채널 조회 성공")
  void findAllByTypeOrIdIn_success() {
    // given
    Channel publicChannel = persistChannel(ChannelType.PUBLIC, "public", "공개 채널");
    Channel privateChannel = persistChannel(ChannelType.PRIVATE, null, null);
    Channel otherPrivateChannel = persistChannel(ChannelType.PRIVATE, null, null);

    entityManager.flush();
    entityManager.clear();

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(privateChannel.getId())
    );

    // then
    assertThat(result)
        .extracting(Channel::getId)
        .contains(publicChannel.getId(), privateChannel.getId())
        .doesNotContain(otherPrivateChannel.getId());
  }

  @Test
  @DisplayName("채널 조회 실패 - 조건에 맞는 채널 없음")
  void findAllByTypeOrIdIn_fail_empty() {
    // given
    Channel privateChannel = persistChannel(ChannelType.PRIVATE, null, null);

    entityManager.flush();
    entityManager.clear();

    // when
    List<Channel> result = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        List.of(UUID.randomUUID())
    );

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("채널 이름 기준 정렬 조회 성공")
  void findAll_sortByName_success() {
    // given
    persistChannel(ChannelType.PUBLIC, "bbb", "bbb 설명");
    persistChannel(ChannelType.PUBLIC, "aaa", "aaa 설명");

    entityManager.flush();
    entityManager.clear();

    // when
    List<Channel> result = channelRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

    // then
    assertThat(result)
        .extracting(Channel::getName)
        .containsExactly("aaa", "bbb");
  }

  private Channel persistChannel(ChannelType type, String name, String description) {
    Channel channel = new Channel(type, name, description);
    entityManager.persist(channel);
    return channel;
  }
}