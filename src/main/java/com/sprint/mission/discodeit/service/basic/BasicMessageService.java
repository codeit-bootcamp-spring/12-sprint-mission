package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.data.request.message.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.data.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.data.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.message.BinaryContent;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        if (channelRepository.findById(request.channelId()).isEmpty()) {
            throw new IllegalArgumentException("BasicMessageService 채널 id 못 찾음");
        }

        if (userRepository.findById(request.authorId()).isEmpty()) {
            throw new IllegalArgumentException("BasicMessageService user id 못 찾음");
        }

        List<BinaryContentCreateRequest> binaryContentRequestList = request.binarRequestList().orElseGet(List::of);

        List<UUID> attachmentIds = new ArrayList<>();
        for (BinaryContentCreateRequest bccr : binaryContentRequestList) {
            BinaryContent binaryContent = new BinaryContent(bccr.fileName(), bccr.fileData());
            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

            attachmentIds.add(savedBinaryContent.getId());
        }

        Message message = new Message(request.authorId(), request.channelId(), request.content(), attachmentIds);
        messageRepository.save(message);

        binaryContentRepository.save(new BinaryContent("", ""));

        return message;
    }

    public List<UUID> getAttachmentIds(Message message) {
        return message.getAttachmentIds();
    }

    @Override
    public Message find(UUID id) {
        Optional<Message> message = messageRepository.findById(id);

        if (message.isPresent()) {
            return message.get();
        } else {
            throw new IllegalArgumentException("id 없음");
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.findById(id).orElse(null);

        if (message == null) {
            throw new IllegalArgumentException("해당 id를 가진 메시지 없음.");
        }

        message.update(request.content(), request.attachmentIds());

        return messageRepository.save(message);
    }

    @Override
    public Message delete(UUID id) {
        Message deletedMessage = messageRepository.deleteById(id);

        List<UUID> attachmentIds = deletedMessage.getAttachmentIds();
        if (attachmentIds != null) {
            for (UUID attachmentId : attachmentIds) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }

        return deletedMessage;
    }
}