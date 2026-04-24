package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
	private final BinaryContentService binaryContentService;

	@RequestMapping(path = "/find/{binaryContentId}", method = RequestMethod.GET)
	public ResponseEntity<BinaryContent> findByIds(@PathVariable UUID binaryContentId) {
		BinaryContent binaryContents = binaryContentService.find(binaryContentId);
		return ResponseEntity.ok(binaryContents);
	}

	@RequestMapping(path = "/findAllByIdIn", method = RequestMethod.GET)
	public ResponseEntity<List<BinaryContent>> findByIds(@RequestParam(value = "id") List<UUID> binaryContentIds) {
		List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
		return ResponseEntity.ok(binaryContents);
	}
}
