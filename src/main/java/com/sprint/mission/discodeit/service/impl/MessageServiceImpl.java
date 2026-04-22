package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("messageServiceImpl")
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto create(MessageCreateRequest request) {
        UUID channelId = request.channelId();
        UUID userId = request.userId();
        String content = request.content();
        List<BinaryContentCreateRequest> attachments = request.attachments();

        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. id=" + channelId));

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        Message message = Message.builder()
                .content(content)
                .userId(userId)
                .channelId(channelId)
                .build();

        message = messageRepository.save(message);

        if (attachments != null && !attachments.isEmpty()) {
            for (BinaryContentCreateRequest attachment : attachments) {
                BinaryContent binaryContent = BinaryContent.builder()
                        .userId(userId)
                        .messageId(message.getId())
                        .binaryData(attachment.binaryData())
                        .build();

                binaryContentRepository.save(binaryContent);
            }
        }

        return MessageDto.from(message);
    }

    @Override
    public MessageDto findById(UUID id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id가 없습니다."));

        return MessageDto.from(message);
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        return messages.stream()
                .map(MessageDto::from)
                .toList();
    }

    @Override
    public MessageDto update(MessageUpdateRequest request) {
        UUID id = request.id();
        String content = request.content();

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + id));

        message.update(content);
        message = messageRepository.save(message);

        return MessageDto.from(message);
    }

    @Override
    public MessageDto delete(UUID id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByMessageId(message.getId());

        for (BinaryContent binaryContent : binaryContents) {
            binaryContentRepository.delete(binaryContent.getId());
        }

        messageRepository.delete(message.getId());
        return MessageDto.from(message);
    }
}
