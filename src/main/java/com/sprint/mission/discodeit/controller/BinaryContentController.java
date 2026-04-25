package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService){
        this.binaryContentService = binaryContentService;
    }

    // 파일 1개 조회
    @RequestMapping(value="/{binaryContentId}", method = RequestMethod.GET)
    public BinaryContent find(
            @PathVariable UUID binaryContentId
    ){
        return binaryContentService.find(binaryContentId);
    }

    // 파일 여러개 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> findAllByIdIn(
            @RequestParam List<UUID> ids
    ){
        return binaryContentService.findAllByIdIn(ids);
    }

    @RequestMapping(value="/{binaryContentId}", method = RequestMethod.DELETE)
    public void delete(
            @PathVariable UUID binaryContentId
    ){
        binaryContentService.delete(binaryContentId);
    }
}