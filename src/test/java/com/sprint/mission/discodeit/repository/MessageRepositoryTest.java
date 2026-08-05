package com.sprint.mission.discodeit.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import com.sprint.mission.discodeit.entity.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserStatusRepository userStatusRepository;

  private Channel savedChannel;
  private User savedUser;

  @BeforeEach
  void setUp() {
    savedChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "공지", null));
    savedUser = userRepository.save(new User("JaneDoe", "JaneDoe@codeit.com", "pw", null, Role.ADMIN));
    // UserStatus 필수 - JOIN FETCH a.status
    userStatusRepository.save(new UserStatus(savedUser, Instant.now()));

    messageRepository.save(new Message("첫번째 메시지", savedChannel, savedUser, List.of()));
    messageRepository.save(new Message("두번째 메시지", savedChannel, savedUser, List.of()));
  }

  @Test
  @DisplayName("채널 ID로 메시지 커서 기반 페이징 조회 성공")
  void findAllByChannelIdWithAuthor_success() {
    // given
    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant future = Instant.now().plusSeconds(10); // createdAt < future 조건

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        savedChannel.getId(),
        future,
        pageable
    );

    // then
    assertThat(result.getContent())
        .extracting(Message::getContent)
        .containsExactlyInAnyOrder("첫번째 메시지", "두번째 메시지");
  }

  @Test
  @DisplayName("커서 이전 메시지만 조회")
  void findAllByChannelIdWithAuthor_beforeCursor() {
    // given
    PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant past = Instant.now().minusSeconds(10); // createdAt < past 조건 → 결과 없음

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        savedChannel.getId(),
        past,
        pageable
    );

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  @DisplayName("메시지가 createdAt DESC 정렬")
  void findAllByChannelIdWithAuthor_sortedByCreatedAtDesc() {
    // given
    PageRequest pageable = PageRequest.of(0, 10,
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant cursor = Instant.now().plusSeconds(10);

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        savedChannel.getId(), cursor, pageable);

    // then
    List<Message> messages = result.getContent();
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0).getCreatedAt())
        .isAfterOrEqualTo(messages.get(1).getCreatedAt()); // DESC 검증
  }

  @Test
  @DisplayName("페이지 크기 초과 시 hasNext true 반환")
  void findAllByChannelIdWithAuthor_hasNextTrue() {
    // given - 3개 저장 후 pageSize=2로 조회
    messageRepository.save(new Message("세번째", savedChannel, savedUser, List.of()));
    PageRequest pageable = PageRequest.of(0, 2,
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant cursor = Instant.now().plusSeconds(10);

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        savedChannel.getId(), cursor, pageable);

    // then
    assertThat(result.hasNext()).isTrue();
    assertThat(result.getContent()).hasSize(2);
  }

  @Test
  @DisplayName("마지막 페이지에서 hasNext false 반환")
  void findAllByChannelIdWithAuthor_hasNextFalse() {
    // given - 데이터 2개, pageSize=10
    PageRequest pageable = PageRequest.of(0, 10,
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Instant cursor = Instant.now().plusSeconds(10);

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
        savedChannel.getId(), cursor, pageable);

    // then
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("채널 ID로 마지막 메시지 시각 조회 성공")
  void findLastMessageAtByChannelId_success() {
    // when
    Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(
        savedChannel.getId()
    );

    // then
    assertThat(lastMessageAt).isPresent();
  }

  @Test
  @DisplayName("메시지 없는 채널의 마지막 메시지 시각 조회 - 빈 Optional 반환")
  void findLastMessageAtByChannelId_empty() {
    // given
    Channel emptyChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "빈채널", null));

    // when
    Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(
        emptyChannel.getId()
    );

    // then
    assertThat(lastMessageAt).isEmpty();
  }

  @Test
  @DisplayName("채널 ID로 메시지 전체 삭제 성공")
  void deleteAllByChannelId_success() {
    // when
    messageRepository.deleteAllByChannelId(savedChannel.getId());

    // then
    Slice<Message> remaining = messageRepository.findAllByChannelIdWithAuthor(
        savedChannel.getId(), Instant.now().plusSeconds(10),
        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
    );
    assertThat(remaining.getContent()).isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 채널 ID로 삭제 - 예외 없이 정상 처리")
  void deleteAllByChannelId_notExist() {
    // when & then
    assertThatCode(() ->
        messageRepository.deleteAllByChannelId(UUID.randomUUID())
    ).doesNotThrowAnyException();
  }
}