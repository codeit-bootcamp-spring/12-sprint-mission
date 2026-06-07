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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private TestEntityManager em;

  private Channel channel;
  private User author;
  private Message message1;
  private Message message2;

  @BeforeEach
  void setUp() {
    author = new User("testUser", "test@example.com", "password123", null);
    em.persist(author);

    UserStatus status = new UserStatus(author, Instant.now());
    em.persist(status);

    channel = new Channel(ChannelType.PUBLIC, "테스트채널", null);
    em.persist(channel);

    message1 = new Message("첫번째 메시지", channel, author, List.of());
    em.persist(message1);

    message2 = new Message("두번째 메시지", channel, author, List.of());
    em.persist(message2);

    em.flush();
    em.clear();
  }

  // ── findAllByChannelIdWithAuthor ─────────────────────────

  @Test
  @DisplayName("채널 메시지 목록 조회 성공")
  void findAllByChannelIdWithAuthor_Success() {
    // given
    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.now(),
        pageable
    );

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getAuthor().getUsername()).isEqualTo("testUser");
  }

  @Test
  @DisplayName("cursor 이전 메시지만 조회됨")
  void findAllByChannelIdWithAuthor_WithCursor_ReturnsOnlyBefore() {
    // given
    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant cursor = message1.getCreatedAt();

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        cursor,
        pageable
    );

    // then
    assertThat(result.getContent()).allMatch(m -> m.getCreatedAt().isBefore(cursor));
  }

  // ── findLastMessageAtByChannelId ─────────────────────────

  @Test
  @DisplayName("채널의 마지막 메시지 시간 조회 성공")
  void findLastMessageAtByChannelId_Success() {
    // when
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("메시지 없는 채널의 마지막 메시지 시간 조회 시 빈 Optional 반환")
  void findLastMessageAtByChannelId_NoMessages_ReturnsEmpty() {
    // given
    Channel emptyChannel = new Channel(ChannelType.PUBLIC, "빈채널", null);
    em.persist(emptyChannel);
    em.flush();
    em.clear();

    // when
    Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(emptyChannel.getId());

    // then
    assertThat(result).isEmpty();
  }

  // ── deleteAllByChannelId ─────────────────────────────────

  @Test
  @DisplayName("채널 메시지 전체 삭제 성공")
  void deleteAllByChannelId_Success() {
    // when
    messageRepository.deleteAllByChannelId(channel.getId());
    em.flush();
    em.clear();

    // then
    PageRequest pageable = PageRequest.of(0, 10);
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        channel.getId(),
        Instant.now(),
        pageable
    );
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  @DisplayName("다른 채널 메시지는 삭제되지 않음")
  void deleteAllByChannelId_OtherChannelNotAffected() {
    // given
    Channel otherChannel = new Channel(ChannelType.PUBLIC, "다른채널", null);
    em.persist(otherChannel);
    Message otherMessage = new Message("다른채널 메시지", otherChannel, author, List.of());
    em.persist(otherMessage);
    em.flush();
    em.clear();

    // when
    messageRepository.deleteAllByChannelId(channel.getId());
    em.flush();
    em.clear();

    // then
    PageRequest pageable = PageRequest.of(0, 10);
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        otherChannel.getId(),
        Instant.now(),
        pageable
    );
    assertThat(result.getContent()).hasSize(1);
  }
}