package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

@Service("binaryContentService")
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
	private final BinaryContentRepository binaryContentRepository;
	@Override
	public BinaryContent create(BinaryContentCreateRequestDto dto) {
		return binaryContentRepository.save(BinaryContent.builder()
			.content(dto.content())
			.build());
	}

	@Override
	public BinaryContent find(UUID id) {
		return binaryContentRepository.findById(id).orElse(null);
	}

	@Override
	public List<BinaryContent> findAllByIdIn(UUID id) {
		return binaryContentRepository.findAll().stream()
			.filter(b -> b.getId().equals(id))
			.toList();
	}

	@Override
	public void delete(UUID id) {
		if (!binaryContentRepository.existsById(id)) {
			throw new IllegalArgumentException("BinaryContent with id " + id + " not found");
		}
		binaryContentRepository.deleteById(id);
	}
}
