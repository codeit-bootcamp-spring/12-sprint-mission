package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@Service("readStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	@Override
	public ReadStatus create(ReadStatusCreateRequestDto dto) {
		if (!userRepository.existsById(dto.userId())){
			throw new IllegalArgumentException("User with id " + dto.userId() + " not found");
		}
		if (!channelRepository.existsById(dto.channelId())){
			throw new IllegalArgumentException("Channel with id " + dto.channelId() + " not found");
		}
		return readStatusRepository.findAll().stream()
			.filter(r -> r.getId().equals(dto.userId()) && r.getChannelId().equals(dto.channelId()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("ReadStatus for user " + dto.userId() + " and channel " + dto.channelId() + " not found"));
	}

	@Override
	public ReadStatus find(UUID id) {
		return readStatusRepository.findById(id).orElse(null);
	}

	@Override
	public List<ReadStatus> findAllByUserId(UUID userId) {
		if (!userRepository.existsById(userId)){
			throw new IllegalArgumentException("User with id " + userId + " not found");
		}
		return readStatusRepository.findAll().stream()
			.filter(r -> r.getUserId().equals(userId))
			.toList();
	}

	@Override
	public ReadStatus update(ReadStatusUpdateRequestDto dto) {
		if (readStatusRepository.existsById(dto.id())){
			ReadStatus readStatus = readStatusRepository.findById(dto.id())
				.orElseThrow(() -> new IllegalArgumentException("ReadStatus with id " + dto.id() + " not found"));
			readStatus.update(dto.lastReadAt());
			return readStatusRepository.save(readStatus);
		}

		throw new IllegalArgumentException("ReadStatus with id " + dto.id() + " not found");
	}

	@Override
	public void delete(UUID id) {
		if (!readStatusRepository.existsById(id)){
			throw new IllegalArgumentException("ReadStatus with id " + id + " not found");
		}
		readStatusRepository.deleteById(id);
	}
}
