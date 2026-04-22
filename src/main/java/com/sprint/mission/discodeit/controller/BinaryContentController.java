package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> findAll(
            @RequestParam List<UUID> ids
    ) {
        List<BinaryContentResponse> binaryContentResponseList = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(binaryContentResponseList);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> find(@RequestParam UUID binaryContentId) {
        BinaryContentResponse binaryContentResponse = binaryContentService.findById(binaryContentId);
        return ResponseEntity.ok(binaryContentResponse);
    }
}