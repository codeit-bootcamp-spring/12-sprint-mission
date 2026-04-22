package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelCreatePrivateRequestDto;
import com.sprint.mission.discodeit.dto.request.ChannelCreatePublicRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@Service("channelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MessageRepository messageRepository;

	@Override
	public Channel create(ChannelCreatePrivateRequestDto dto) {
		if (dto.users() == null || dto.users().isEmpty()){
			throw new IllegalArgumentException("users cannot be empty");
		}
		Channel channel = new Channel(ChannelType.PRIVATE, null, null);
		dto.users().forEach(u -> readStatusRepository.save(new ReadStatus(u.getId(), channel.getId(), Instant.now())));
		return channelRepository.save(channel);
	}

	@Override
	public Channel create(ChannelCreatePublicRequestDto dto) {
		Channel channel = new Channel(ChannelType.PUBLIC, dto.name(), dto.description());
		return channelRepository.save(channel);
	}

	@Override
	public ChannelDto find(UUID channelId) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
		Instant lastMessageAt = messageRepository.findAll()
			.stream().filter(m -> m.getChannelId().equals(channelId))
			.map(Message::getCreatedAt)
			.max(Instant::compareTo)
			.orElse(null);
		List<UUID> userIds = null;
		if (channel.getType() == ChannelType.PRIVATE) {
			userIds = readStatusRepository.findAll()
				.stream().filter(r -> r.getChannelId().equals(channelId))
				.map(ReadStatus::getUserId).toList();
		}
		return ChannelDto.from(channel, lastMessageAt, userIds);
	}

	@Override
	public List<ChannelDto> findAllByUserId(UUID userId) {
		List<Channel> channelList = channelRepository.findAll().stream()
								.filter(c -> c.getType().equals(ChannelType.PUBLIC) ||
										(c.getType().equals(ChannelType.PRIVATE) &&
												readStatusRepository.findAll().stream()
														.anyMatch(r -> r.getChannelId().equals(c.getId()) && r.getUserId().equals(userId)))).toList();
		List<ChannelDto> channelDtoList = new ArrayList<>();
		for (Channel channel : channelList) {
			Instant lastMessageAt = messageRepository.findAll()
				.stream().filter(m -> m.getChannelId().equals(channel.getId()))
				.map(Message::getCreatedAt)
				.max(Instant::compareTo)
				.orElse(null);
			if (channel.getType().equals(ChannelType.PRIVATE)) {
				List<UUID> userIds = readStatusRepository.findAll()
					.stream().filter(r -> r.getChannelId().equals(channel.getId()))
					.map(ReadStatus::getUserId).toList();
				channelDtoList.add(ChannelDto.from(channel, lastMessageAt, userIds));
			}else if  (channel.getType().equals(ChannelType.PUBLIC)) {
				channelDtoList.add(ChannelDto.from(channel, lastMessageAt, null));
			}
		}
		return channelDtoList;
	}

	@Override
	public Channel update(UUID channelId, ChannelUpdateRequestDto dto) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
		if (channel.getType().equals(ChannelType.PRIVATE)){
			throw new IllegalArgumentException("Cannot update private channel");
		}
		channel.update(dto.newName(), dto.newDescription());
		return channelRepository.save(channel);
	}

	@Override
	public void delete(UUID channelId) {
		if (!channelRepository.existsById(channelId)) {
			throw new NoSuchElementException("Channel with id " + channelId + " not found");
		}
		messageRepository.findAll().stream().filter(m -> m.getChannelId().equals(channelId))
				.forEach(m -> messageRepository.deleteById(m.getId()));
		readStatusRepository.findAll().stream().filter(r -> r.getChannelId().equals(channelId))
				.forEach(r -> readStatusRepository.deleteById(r.getId()));
		channelRepository.deleteById(channelId);
	}
}
