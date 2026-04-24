package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-contents")
@AllArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        BinaryContent binaryContent = binaryContentService.find(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(binaryContent.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + binaryContent.getFileName() + "\""
                )
                .body(binaryContent.getBytes());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> ids) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(binaryContents);
    }
}
