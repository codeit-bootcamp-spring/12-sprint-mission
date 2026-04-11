package com.sprint.mission.discodeit.service.Impl;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("messageService")
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {

        if(!channelRepository.existsById(request.channelId())){
            throw new NoSuchElementException("존재하지 않는 채널에 메시지를 작성할 수 없습니다.");
        }
        if(!userRepository.existsById(request.userId())){
            throw new NoSuchElementException("존재하지 않는 채널에 메시지를 작성할 수 없습니다.");
        }
        Message message = new Message(
                request.channelId(),
                request.userId(),
                request.title(),
                request.content(),
                request.attachmentIds()
        );
        return convertToResponse(messageRepository.save(message));
    }

    public MessageResponse convertToResponse(Message message){
        return new MessageResponse(
                message.getChannelId(),
                message.getUserId(),
                message.getTitle(),
                message.getContent(),
                message.getAttachmentIds()
        );
    }

    @Override
    public List<MessageResponse> findByChannelId(UUID id) {
        List<Message> messages = messageRepository.findByChannelId(id);
        if (messages == null || messages.isEmpty()) {
            throw new NoSuchElementException("해당 Message가 존재하지 않습니다.");
        }
        List<MessageResponse> messageResponses = new ArrayList<>();
        messages.forEach(message -> messageResponses.add(convertToResponse(message)));

        return messageResponses;
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.id());
        if (message == null) throw new NoSuchElementException("해당 Message가 존재하지 않습니다.");

        message.update(
                request.title(),
                request.content()
        );
        Message updatedMessage = messageRepository.save(message);
        return convertToResponse(updatedMessage);
    }

    @Override
    public void delete(UUID id) {
        if (messageRepository.existsById(id)){
            throw new NoSuchElementException("해당 메시지가 존재하지 않습니다.");
        }

        List<UUID> attachmentIds = messageRepository.findById(id).getAttachmentIds();
        attachmentIds.forEach(binaryContentRepository::deleteById);

        messageRepository.delete(id);
    }
}











