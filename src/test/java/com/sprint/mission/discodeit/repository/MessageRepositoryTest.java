package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
class MessageRepositoryTest {

  @Autowired MessageRepository messageRepository;
  @Autowired ChannelRepository channelRepository;
  @Autowired UserRepository userRepository;

  private Channel channel;
  private User author;

  @BeforeEach
  void setUp() {
    author = userRepository.save(new User("author", "author@email.com", "pass", null));
    channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", null));
    messageRepository.save(new Message("msg1", channel, author, List.of()));
    messageRepository.save(new Message("msg2", channel, author, List.of()));
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 성공")
  void findAllByChannel_Id_success() {
    List<Message> messages = messageRepository.findAllByChannel_Id(channel.getId());
    assertThat(messages).hasSize(2);
    assertThat(messages).allMatch(m -> m.getChannel().getId().equals(channel.getId()));
  }

  @Test
  @DisplayName("다른 채널의 메시지는 조회되지 않음")
  void findAllByChannel_Id_otherChannel_empty() {
    Channel other = channelRepository.save(new Channel(ChannelType.PUBLIC, "other", null));
    List<Message> messages = messageRepository.findAllByChannel_Id(other.getId());
    assertThat(messages).isEmpty();
  }

  @Test
  @DisplayName("커서 기반 페이지네이션 - cursor null이면 최신 메시지부터")
  void findByChannelIdWithCursor_noCursor() {
    Slice<Message> slice = messageRepository.findByChannelIdWithCursor(
        channel.getId(), null, PageRequest.of(0, 10));
    assertThat(slice.getContent()).hasSize(2);
  }

  @Test
  @DisplayName("커서 기반 페이지네이션 - cursor 이전 메시지만 반환")
  void findByChannelIdWithCursor_withCursor() {
    // 미래 시각을 cursor로 설정하면 저장된 메시지 모두가 cursor 이전이므로 조회됨
    Instant future = Instant.now().plusSeconds(3600);
    Slice<Message> slice = messageRepository.findByChannelIdWithCursor(
        channel.getId(), future, PageRequest.of(0, 10));
    assertThat(slice.getContent()).hasSize(2);
  }
}
