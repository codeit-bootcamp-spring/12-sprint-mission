package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("채널의 메시지를 최신순으로 조회할 수 있다")
  void findAllByChannelOrderByCreatedAtDesc_success() throws InterruptedException {
    // given
    User user = saveUser("user@test.com", "user");
    Channel channel = savePublicChannel("공개 채널");

    Message message1 = new Message(
        "첫 번째 메시지",
        channel,
        user,
        List.of()
    );
    messageRepository.saveAndFlush(message1);

    Thread.sleep(10);

    Message message2 = new Message(
        "두 번째 메시지",
        channel,
        user,
        List.of()
    );
    messageRepository.saveAndFlush(message2);

    // when
    Slice<Message> result = messageRepository.findAllByChannelOrderByCreatedAtDesc(
        channel,
        PageRequest.of(0, 50)
    );

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getContent()).isEqualTo("두 번째 메시지");
    assertThat(result.getContent().get(1).getContent()).isEqualTo("첫 번째 메시지");
  }

  @Test
  @DisplayName("해당 채널에 메시지가 없으면 빈 Slice를 반환한다")
  void findAllByChannelOrderByCreatedAtDesc_empty() {
    // given
    Channel channel = savePublicChannel("공개 채널");

    // when
    Slice<Message> result = messageRepository.findAllByChannelOrderByCreatedAtDesc(
        channel,
        PageRequest.of(0, 50)
    );

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  @DisplayName("채널의 가장 최근 메시지를 조회할 수 있다")
  void findTopByChannelOrderByCreatedAtDesc_success() throws InterruptedException {
    // given
    User user = saveUser("user@test.com", "user");
    Channel channel = savePublicChannel("공개 채널");

    Message oldMessage = new Message(
        "이전 메시지",
        channel,
        user,
        List.of()
    );
    messageRepository.saveAndFlush(oldMessage);

    Thread.sleep(10);

    Message latestMessage = new Message(
        "최근 메시지",
        channel,
        user,
        List.of()
    );
    messageRepository.saveAndFlush(latestMessage);

    // when
    Optional<Message> result = messageRepository.findTopByChannelOrderByCreatedAtDesc(channel);

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getContent()).isEqualTo("최근 메시지");
  }

  @Test
  @DisplayName("채널에 메시지가 없으면 가장 최근 메시지 조회 결과가 empty이다")
  void findTopByChannelOrderByCreatedAtDesc_empty() {
    // given
    Channel channel = savePublicChannel("공개 채널");

    // when
    Optional<Message> result = messageRepository.findTopByChannelOrderByCreatedAtDesc(channel);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("채널 ID로 메시지를 삭제할 수 있다")
  void deleteByChannelId_success() {
    // given
    User user = saveUser("user@test.com", "user");
    Channel channel = savePublicChannel("공개 채널");

    Message message1 = new Message(
        "첫 번째 메시지",
        channel,
        user,
        List.of()
    );

    Message message2 = new Message(
        "두 번째 메시지",
        channel,
        user,
        List.of()
    );

    messageRepository.save(message1);
    messageRepository.save(message2);

    // when
    messageRepository.deleteByChannelId(channel.getId());

    // then
    List<Message> messages = messageRepository.findAll();
    assertThat(messages).isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 채널 ID로 삭제해도 기존 메시지는 삭제되지 않는다")
  void deleteByChannelId_fail_notFoundChannelId() {
    // given
    User user = saveUser("user@test.com", "user");
    Channel channel = savePublicChannel("공개 채널");

    Message message = new Message(
        "삭제되면 안 되는 메시지",
        channel,
        user,
        List.of()
    );

    messageRepository.save(message);

    UUID unknownChannelId = UUID.randomUUID();

    // when
    messageRepository.deleteByChannelId(unknownChannelId);

    // then
    List<Message> messages = messageRepository.findAll();
    assertThat(messages).hasSize(1);
    assertThat(messages.get(0).getContent()).isEqualTo("삭제되면 안 되는 메시지");
  }

  private User saveUser(String email, String username) {
    User user = new User(
        email,
        username,
        "password",
        null
    );
    user.initStatus();

    return userRepository.save(user);
  }

  private Channel savePublicChannel(String name) {
    Channel channel = Channel.createPublic(
        name,
        name + " 설명"
    );

    return channelRepository.save(channel);
  }
}