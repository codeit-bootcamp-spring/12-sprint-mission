package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 성공")
  void findAllByChannelIdWithAuthor_success() {
    // given
    Channel channel = persistChannel(ChannelType.PUBLIC, "public", "공개 채널");
    User author = persistUserWithStatus("minji", "minji@test.com");

    Message message1 = persistMessage("첫 번째 메시지", channel, author);
    Message message2 = persistMessage("두 번째 메시지", channel, author);

    entityManager.flush();
    entityManager.clear();

    Pageable pageable = PageRequest.of(
        0,
        10,
        Sort.by(Sort.Direction.DESC, "createdAt")
    );

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.now().plusSeconds(60),
        pageable
    );

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent())
        .extracting(Message::getContent)
        .contains("첫 번째 메시지", "두 번째 메시지");
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 실패 - 조건에 맞는 메시지 없음")
  void findAllByChannelIdWithAuthor_fail_empty() {
    // given
    Channel channel = persistChannel(ChannelType.PUBLIC, "public", "공개 채널");
    User author = persistUserWithStatus("minji", "minji@test.com");

    persistMessage("메시지", channel, author);

    entityManager.flush();
    entityManager.clear();

    Pageable pageable = PageRequest.of(
        0,
        10,
        Sort.by(Sort.Direction.DESC, "createdAt")
    );

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.EPOCH,
        pageable
    );

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  @DisplayName("채널의 마지막 메시지 시간 조회 성공")
  void findLastMessageAtByChannelId_success() {
    // given
    Channel channel = persistChannel(ChannelType.PUBLIC, "public", "공개 채널");
    User author = persistUserWithStatus("minji", "minji@test.com");

    persistMessage("첫 번째 메시지", channel, author);
    persistMessage("두 번째 메시지", channel, author);

    entityManager.flush();
    entityManager.clear();

    // when
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("채널의 마지막 메시지 시간 조회 실패 - 메시지 없음")
  void findLastMessageAtByChannelId_fail_empty() {
    // given
    Channel channel = persistChannel(ChannelType.PUBLIC, "public", "공개 채널");

    entityManager.flush();
    entityManager.clear();

    // when
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("채널 ID로 메시지 삭제 성공")
  void deleteAllByChannelId_success() {
    // given
    Channel channel = persistChannel(ChannelType.PUBLIC, "public", "공개 채널");
    User author = persistUserWithStatus("minji", "minji@test.com");

    persistMessage("첫 번째 메시지", channel, author);
    persistMessage("두 번째 메시지", channel, author);

    entityManager.flush();
    entityManager.clear();

    // when
    messageRepository.deleteAllByChannelId(channel.getId());
    entityManager.flush();
    entityManager.clear();

    // then
    List<Message> result = messageRepository.findAll();
    assertThat(result).isEmpty();
  }

  private Channel persistChannel(ChannelType type, String name, String description) {
    Channel channel = new Channel(type, name, description);
    entityManager.persist(channel);
    return channel;
  }

  private User persistUserWithStatus(String username, String email) {
    User user = new User(username, email, "1234", null);
    entityManager.persist(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    entityManager.persist(userStatus);

    return user;
  }

  private Message persistMessage(String content, Channel channel, User author) {
    Message message = new Message(content, channel, author, List.of());
    entityManager.persist(message);
    return message;
  }
}