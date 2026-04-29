package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public BinaryContent findBinaryContent(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> findBinaryContents(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }

    @RequestMapping(value = "/{binaryContentId}/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadBinaryContent(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + binaryContent.getFileName() + "\""
                )
                .header(HttpHeaders.CONTENT_TYPE, binaryContent.getContentType())
                .body(binaryContent.getBytes());
    }
}
