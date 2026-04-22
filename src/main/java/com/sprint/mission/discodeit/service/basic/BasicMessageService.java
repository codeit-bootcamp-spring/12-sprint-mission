package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.sprint.mission.discodeit.entity.BinaryContent.createProfileImage;

@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicMessageService(
            @Qualifier("jCFMessageRepository") MessageRepository messageRepository,
            @Qualifier("jCFChannelRepository") ChannelRepository channelRepository,
            @Qualifier("jCFUserRepository") UserRepository userRepository,
            @Qualifier("jCFBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public MessageResponseDto create(MessageCreateRequestDto dto) {
        if (!channelRepository.existsById(dto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + dto.channelId());
        }
        if (!userRepository.existsById(dto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + dto.authorId());
        }

        Message message = new Message(dto.content(), dto.channelId(), dto.authorId());
        messageRepository.save(message);

        List<UUID> messageBinaryContentIds = new ArrayList<>();

        if (!dto.files().isEmpty()) {
            for (byte[] fileData : dto.files()) {
                BinaryContent binaryContent = BinaryContent.createMessageImage(
                        dto.authorId(),
                        message.getId(),
                        fileData
                );
                binaryContentRepository.save(binaryContent);
                messageBinaryContentIds.add(binaryContent.getId());
            }

        }

        return MessageResponseDto.from(message, messageBinaryContentIds);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }

        List<Message> messages = messageRepository.findAll();
        List<MessageResponseDto> dtos = new ArrayList<>();

        for (Message message : messages) {
            if (message.getChannelId().equals(channelId)) {
                List<UUID> binaryContentIds = binaryContentRepository.findByMessageId(message.getId());
                dtos.add(MessageResponseDto.from(message, binaryContentIds));
            }

        }
        return dtos;

    }

    @Override
    public MessageResponseDto update(MessageUpdateRequestDto dto) {
        Message message = messageRepository.findById(dto.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + dto.messageId() + " not found"));
        message.update(dto.content());
        messageRepository.save(message);

        List<UUID> currentBinaryContentIds = binaryContentRepository.findByMessageId(message.getId());

        if (dto.files() != null) {
            binaryContentRepository.deleteByMessageId(dto.messageId());

            List<UUID> newBinaryContentIds = new ArrayList<>();

                for (byte[] fileData : dto.files()) {
                    BinaryContent binaryContent = BinaryContent.createMessageImage(
                            message.getAuthorId(),
                            message.getId(),
                            fileData
                    );
                    binaryContentRepository.save(binaryContent);
                    newBinaryContentIds.add(binaryContent.getId());
                }
                currentBinaryContentIds = newBinaryContentIds;
        }
        return MessageResponseDto.from(message, currentBinaryContentIds);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        binaryContentRepository.deleteByMessageId(messageId);
        messageRepository.deleteById(messageId);
    }
}
