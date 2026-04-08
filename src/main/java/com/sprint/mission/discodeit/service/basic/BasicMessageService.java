package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service("messageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
	private final MessageRepository messageRepository;
	//
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;

	@Override
	public Message create(MessageCreateRequestDto dto) {
		if (!channelRepository.existsById(dto.channelId())) {
			throw new NoSuchElementException("Channel not found with id " + dto.channelId());
		}
		if (!userRepository.existsById(dto.authorId())) {
			throw new NoSuchElementException("Author not found with id " + dto.authorId());
		}

		Message message = new Message(dto.content(), dto.channelId(), dto.authorId(), null);
		return messageRepository.save(message);
	}

	@Override
	public Message find(UUID messageId) {
		return messageRepository.findById(messageId)
			.orElse(null);
	}

	@Override
	public List<Message> findAllChannelId(UUID channelId) {
		return messageRepository.findAll().stream().filter(m -> m.getChannelId().equals(channelId)).toList();
	}

	@Override
	public Message update(MessageUpdateRequestDto dto) {
		Message message = messageRepository.findById(dto.messageId())
			.orElseThrow(() -> new NoSuchElementException("Message with id " + dto.messageId() + " not found"));
		message.update(dto.newContent());
		return messageRepository.save(message);
	}

	@Override
	public void delete(UUID messageId) {
		if (!messageRepository.existsById(messageId)) {
			throw new NoSuchElementException("Message with id " + messageId + " not found");
		}
		messageRepository.findById(messageId)
			.map(Message::getAttachmentIds)
			.stream().flatMap(List::stream)
			.forEach(binaryContentRepository::deleteById);
		messageRepository.deleteById(messageId);
	}
}
