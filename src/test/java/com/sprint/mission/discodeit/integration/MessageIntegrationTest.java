package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

  @Autowired MessageService messageService;
  @Autowired ChannelService channelService;
  @Autowired UserService userService;

  private UserDto user;
  private ChannelDto channel;

  @BeforeEach
  void setUp() {
    user = userService.create(
        new UserCreateRequest("msguser", "msguser@email.com", "password123!"), Optional.empty());
    channel = channelService.create(new PublicChannelCreateRequest("test-channel", null));
  }

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_success() {
    MessageDto msg = messageService.create(
        new MessageCreateRequest("Hello World", channel.id(), user.id()), List.of());

    assertThat(msg).isNotNull();
    assertThat(msg.content()).isEqualTo("Hello World");
    assertThat(msg.channelId()).isEqualTo(channel.id());
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_success() {
    MessageDto created = messageService.create(
        new MessageCreateRequest("Original", channel.id(), user.id()), List.of());

    MessageDto updated = messageService.update(created.id(), new MessageUpdateRequest("Updated"));
    assertThat(updated.content()).isEqualTo("Updated");
  }

  @Test
  @DisplayName("메시지 삭제 후 조회 시 예외 발생")
  void deleteMessage_thenNotFound() {
    MessageDto created = messageService.create(
        new MessageCreateRequest("ToDelete", channel.id(), user.id()), List.of());

    messageService.delete(created.id());

    assertThatThrownBy(() -> messageService.find(created.id()))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 - 커서 없이 첫 페이지")
  void findAllByChannelId_firstPage() {
    messageService.create(new MessageCreateRequest("msg1", channel.id(), user.id()), List.of());
    messageService.create(new MessageCreateRequest("msg2", channel.id(), user.id()), List.of());

    PageResponse<MessageDto> page = messageService.findAllByChannelId(channel.id(), null, 50);
    assertThat(page.content()).hasSize(2);
    assertThat(page.hasNext()).isFalse();
  }
}
