package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        if (binaryContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findAll(@RequestParam(name = "binaryContentIds") List<UUID> binaryContentIds) {
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.findAllByIdIn(binaryContentIds));
    }
}
