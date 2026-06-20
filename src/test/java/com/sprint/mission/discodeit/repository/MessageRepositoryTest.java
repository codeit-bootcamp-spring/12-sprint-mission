package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager em;

  private Message createMessage(User author, Channel channel, String content, Instant createdAt) {
    Message message = Message.builder()
        .author(author)
        .channel(channel)
        .content(content)
        .attachments(new ArrayList<>())
        .createdAt(createdAt.truncatedTo(ChronoUnit.MILLIS))
        .build();
    return messageRepository.save(message);
  }

  private User createUser(String username, String email) {
    BinaryContent profile = new BinaryContent(username + ".jpg", 1024L, "image/png");
    User user = new User(username, email, "password1234!", profile);
    UserStatus status = UserStatus.builder()
        .user(user)
        .lastActiveAt(Instant.now())
        .build();
    user.setUserStatus(status);
    return userRepository.save(user);
  }

  private Channel createChannel(ChannelType type, String name) {
    Channel channel = new Channel(type, name, "description: " + name);
    return channelRepository.save(channel);
  }

  // ── findLastMessageAtByChannelId ────────────────────────────────────────

  @Test
  @DisplayName("채널의 마지막 메시지 조회")
  public void findLastMessageAtByChannelId() {
    User user = createUser("testUser", "test@test.com");
    Channel channel = createChannel(ChannelType.PUBLIC, "testChannel");
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Instant fiveMinutesAgo = now.minus(5, ChronoUnit.MINUTES);
    Instant tenMinutesAgo = now.minus(10, ChronoUnit.MINUTES);
    Instant twentyMinutesAgo = now.minus(20, ChronoUnit.MINUTES);
    Message message1 = createMessage(user, channel, "test1", twentyMinutesAgo);
    Message message2 = createMessage(user, channel, "test2", tenMinutesAgo);
    Message message3 = createMessage(user, channel, "test3", fiveMinutesAgo);

    em.flush();
    em.clear();

    Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(
        channel.getId());
    assertThat(lastMessageAt).isPresent();
    assertThat(lastMessageAt.get().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(
        message3.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
  }

  @Test
  @DisplayName("채널에 메시지가 없는 경우 마지막 메시지 조회")
  public void findLastMessageAtByChannelId_noMessages() {
    Channel channel = createChannel(ChannelType.PUBLIC, "emptyChannel");
    em.flush();
    em.clear();
    Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(
        channel.getId());
    assertThat(lastMessageAt).isEmpty();
  }

  // ── deleteAllByChannelId ────────────────────────────────────────────────

  @Test
  @DisplayName("채널의 전체 메시지 삭제")
  public void deleteAllByChannelId() {
    User user = createUser("testUser", "test@test.com");
    Channel channel = createChannel(ChannelType.PUBLIC, "testChannel");
    Instant now = Instant.now();
    Instant fiveMinutesAgo = now.minus(5, ChronoUnit.MINUTES);
    Instant tenMinutesAgo = now.minus(10, ChronoUnit.MINUTES);
    Instant twentyMinutesAgo = now.minus(20, ChronoUnit.MINUTES);
    Message message1 = createMessage(user, channel, "test1", twentyMinutesAgo);
    Message message2 = createMessage(user, channel, "test2", tenMinutesAgo);
    Message message3 = createMessage(user, channel, "test3", fiveMinutesAgo);

    em.flush();
    em.clear();

    messageRepository.deleteAllByChannelId(channel.getId());
    em.flush();
    em.clear();

    assertThat(messageRepository.count()).isZero();
  }

  // ── findMessagesByChannelIdBeforeCursor ─────────────────────────────────

  @Test
  @DisplayName("채널의 메시지 조회 - 커서 이전")
  public void findMessagesByChannelIdBeforeCursor() {
    User user = createUser("testUser", "test@test.com");
    Channel channel = createChannel(ChannelType.PUBLIC, "testChannel");
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Instant fiveMinutesAgo = now.minus(5, ChronoUnit.MINUTES);
    Instant tenMinutesAgo = now.minus(10, ChronoUnit.MINUTES);
    Instant twentyMinutesAgo = now.minus(20, ChronoUnit.MINUTES);
    Instant oneHourAgo = now.minus(1, ChronoUnit.HOURS);
    Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);
    Message message1 = createMessage(user, channel, "test1", twoHoursAgo);
    Message message2 = createMessage(user, channel, "test2", oneHourAgo);
    Message message3 = createMessage(user, channel, "test3", twentyMinutesAgo);
    Message message4 = createMessage(user, channel, "test4", tenMinutesAgo);
    Message message5 = createMessage(user, channel, "test5", fiveMinutesAgo);

    em.flush();
    em.clear();

    Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
    Slice<Message> messages = messageRepository.findMessagesByChannelIdBeforeCursor(
        channel.getId(), Instant.now(), pageable);

    assertThat(messages.getContent()).hasSize(3);
    assertThat(messages.getContent()).extracting(
            m -> ((Message) m).getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
        .containsExactlyInAnyOrder(
            message5.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
            message4.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
            message3.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
    assertThat(messages.getContent()).extracting("content")
        .containsExactly("test5", "test4", "test3");
    assertThat(messages.hasNext()).isTrue();
    assertThat(messages.getContent()).extracting("content")
        .doesNotContain("test1", "test2");
  }
}
