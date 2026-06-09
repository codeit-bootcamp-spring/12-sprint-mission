package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChannelIntegrationTest {

  @Autowired ChannelService channelService;
  @Autowired UserService userService;

  @Test
  @DisplayName("PUBLIC 채널 생성 및 조회 성공")
  void createPublicChannel_success() {
    ChannelDto created = channelService.create(
        new PublicChannelCreateRequest("general", "General channel"));

    assertThat(created).isNotNull();
    assertThat(created.type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(created.name()).isEqualTo("general");
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 시 예외 발생")
  void updatePrivateChannel_throws() {
    UserDto user = userService.create(
        new UserCreateRequest("chuser", "chuser@email.com", "password123!"), Optional.empty());
    ChannelDto privateChannel = channelService.create(
        new PrivateChannelCreateRequest(List.of(user.id())));

    assertThatThrownBy(() ->
        channelService.update(privateChannel.id(),
            new PublicChannelUpdateRequest("new name", "desc")))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  @Test
  @DisplayName("채널 삭제 후 조회 시 404 예외 발생")
  void deleteChannel_thenNotFound() {
    ChannelDto created = channelService.create(
        new PublicChannelCreateRequest("temp", null));

    channelService.delete(created.id());

    assertThatThrownBy(() -> channelService.find(created.id()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("userId로 채널 목록 조회 - PUBLIC 채널 포함")
  void findAllByUserId_includesPublicChannels() {
    UserDto user = userService.create(
        new UserCreateRequest("listuser", "listuser@email.com", "password123!"), Optional.empty());
    channelService.create(new PublicChannelCreateRequest("pub1", null));
    channelService.create(new PublicChannelCreateRequest("pub2", null));

    List<ChannelDto> channels = channelService.findAllByUserId(user.id());
    assertThat(channels.stream().anyMatch(c -> c.type() == ChannelType.PUBLIC)).isTrue();
  }
}
