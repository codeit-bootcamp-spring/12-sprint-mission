package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.TestSecuritySupport;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
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
public class MessageServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Autowired
  private BinaryContentStorage binaryContentStorage;

  private UserDto userDto;
  private ChannelDto channelDto;
  private final List<UUID> storageIds = new ArrayList<>();

  @BeforeEach
  void setUp() {
    userDto = userService.create(
        new UserCreateRequest("test_user", "test@test.com", "password1!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(userDto.id(), Role.CHANNEL_MANAGER);

    channelDto = channelService.create(
        new PublicChannelCreateRequest("test_channel", "description")
    );
  }

  @Test
  @DisplayName("update_message_replaces_attachment_db")
  void updateMessage_replacesAttachmentDb() {
    byte[] beforeBytes = "test1".getBytes(StandardCharsets.UTF_8);
    byte[] updateBytes = "test2".getBytes(StandardCharsets.UTF_8);

    MessageDto beforeMessageDto = messageService.create(
        new MessageCreateRequest(channelDto.id(), userDto.id(), "content"),
        List.of(new BinaryContentCreateRequest(
            "test1.txt",
            "text/plain",
            beforeBytes
        ))
    );
    UUID oldAttachmentId = beforeMessageDto.attachments().get(0).id();
    storageIds.add(oldAttachmentId);

    MessageDto updatedMessageDto = messageService.update(
        beforeMessageDto.id(),
        new MessageUpdateRequest("newContent"),
        List.of(new BinaryContentCreateRequest(
            "test2.txt",
            "text/plain",
            updateBytes
        ))
    );
    UUID newAttachmentId = updatedMessageDto.attachments().get(0).id();
    storageIds.add(newAttachmentId);

    assertThat(updatedMessageDto.id()).isEqualTo(beforeMessageDto.id());
    assertThat(updatedMessageDto.content()).isEqualTo("newContent");
    assertThat(updatedMessageDto.attachments()).hasSize(1);
    assertThat(newAttachmentId).isNotEqualTo(oldAttachmentId);

//    기존 요구사항이었던 더티체킹을 위해 setter를 사용한 부분에서 문제 발생.
    assertThat(binaryContentRepository.existsById(oldAttachmentId)).isFalse();
    assertThat(binaryContentRepository.existsById(newAttachmentId)).isTrue();

    BinaryContent newAttachment = binaryContentRepository.findById(newAttachmentId).orElseThrow();
    assertThat(newAttachment.getFileName()).isEqualTo("test2.txt");
    assertThat(newAttachment.getContentType()).isEqualTo("text/plain");
    assertThat(newAttachment.getSize()).isEqualTo(updateBytes.length);
  }

  @AfterEach
  void cleanup() {
    TestSecuritySupport.clear();
    storageIds.forEach(binaryContentStorage::delete);
  }
}
