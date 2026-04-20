package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

	private final MessageService messageService;

	@RequestMapping(path = "/", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Message> create(
		@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		if (files == null) {
			return ResponseEntity.ok(messageService.create(messageCreateRequest, Collections.emptyList()));
		}
		List<BinaryContentCreateRequest> binaryContentCreateRequests = files.stream()
			.filter(f -> f != null && !f.isEmpty())
			.map(this::resolveProfileRequest)
			.toList();
		return ResponseEntity.ok(messageService.create(messageCreateRequest, binaryContentCreateRequests));
	}

	@RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH)
	public ResponseEntity<Message> update(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request){
		return ResponseEntity.ok(messageService.update(messageId, request));
	}

	@RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable UUID messageId){
		messageService.delete(messageId);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(path = "/{channelId}", method = RequestMethod.GET)
	public ResponseEntity<List<Message>> findByChannelId(@PathVariable UUID channelId) {
		return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
	}

	private BinaryContentCreateRequest resolveProfileRequest(MultipartFile file) {
		if (file.isEmpty()) {
			return null;
		}
		try {
			return new BinaryContentCreateRequest(file.getOriginalFilename(), file.getContentType(), file.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("프로필 이미지 파일을 읽는 중 오류가 발생했습니다.", e);
		}
	}
}
