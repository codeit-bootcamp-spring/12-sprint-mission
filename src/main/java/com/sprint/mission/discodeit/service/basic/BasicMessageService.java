package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.data.message.MessageResponse;
import com.sprint.mission.discodeit.dto.data.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {

        if (!userRepository.existsById(request.authorId())) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        Message message = new Message(request.content(), request.channelId(), request.authorId());

        if (request.attachments() != null && !request.attachments().isEmpty()) {
            for (MessageCreateRequest.AttachmentData fileData : request.attachments()) {
                BinaryContent attachment = new BinaryContent(
                        fileData.bytes(),
                        fileData.fileName(),
                        fileData.contentType()
                );
                binaryContentRepository.save(attachment);

                message.addAttachmentId(attachment.getId());
            }
        }

        messageRepository.save(message);
        return toResponse(message);
    }

    @Override
    public MessageResponse find(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("메세지를 찾을 수 없습니다."));
        return toResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("메세지를 찾을 수 없습니다."));

        message.update(request.content());
        messageRepository.save(message);

        return toResponse(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }
        messageRepository.deleteById(id);
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .channelId(message.getChannelId())
                .authorId(message.getAuthorId())
                .attachmentIds(message.getAttachmentIds())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}