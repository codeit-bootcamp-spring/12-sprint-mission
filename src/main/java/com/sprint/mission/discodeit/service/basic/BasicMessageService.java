package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentService binaryContentService;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + authorId + " does not exist"));

        List<BinaryContent> attachments = toBinaryContents(binaryContentCreateRequests);

        Message message = new Message(
                messageCreateRequest.content(),
                channel,
                author,
                attachments
        );
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        Slice<Message> messageSlice = messageRepository.findAllByChannel_Id(channelId, pageable);
        Slice<MessageDto> messageDtoSlice = messageSlice.map(messageMapper::toDto);

        return pageResponseMapper.fromSlice(messageDtoSlice);
    }

    @Override
    @Transactional
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(request.newContent());
        return message;
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        messageRepository.delete(message);
    }

    private List<BinaryContent> toBinaryContents(
            List<BinaryContentCreateRequest> binaryContentCreateRequests
    ) {
        if (binaryContentCreateRequests == null || binaryContentCreateRequests.isEmpty()) {
            return List.of();
        }
        return binaryContentCreateRequests.stream()
                .map(binaryContentService::create)
                .toList();
    }
}
