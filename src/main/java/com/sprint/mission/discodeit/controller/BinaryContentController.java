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
@RequestMapping("/api/binaryContent")
@AllArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> download(@RequestParam UUID binaryContentId) {
        BinaryContent binaryContent =
                binaryContentService.find(binaryContentId);

        return ResponseEntity.ok(binaryContent);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> ids) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(binaryContents);
    }
}
