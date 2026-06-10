package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentResponse> binaryContentResponseList =
                binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(binaryContentResponseList);
    }

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> find(@PathVariable UUID binaryContentId) {
        BinaryContentResponse binaryContentResponse = binaryContentService.findById(binaryContentId);
        return ResponseEntity.ok(binaryContentResponse);
    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
        log.info("BinaryContent download requested. binaryContentId={}", binaryContentId);

        BinaryContentResponse binaryContent = binaryContentService.findById(binaryContentId);

        log.info(
                "BinaryContent download started. binaryContentId={}, fileName={}, contentType={}, fileSizeBytes={}",
                binaryContent.id(),
                binaryContent.fileName(),
                binaryContent.contentType(),
                binaryContent.size()
        );

        return binaryContentStorage.download(binaryContent);
    }
}