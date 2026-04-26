package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public BinaryContent createBinaryContent(@RequestBody BinaryContentCreateRequest request){
        return binaryContentService.create(request);
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public BinaryContent findBinaryContent(@PathVariable UUID binaryContentId){
        return binaryContentService.find(binaryContentId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContent> findAllByIdInBinaryContent(@RequestParam List<UUID> binaryContentIds){
        return binaryContentService.findAllByIdIn(binaryContentIds);
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.DELETE)
    public void deleteBinaryContent(@PathVariable UUID binaryContentId){
        binaryContentService.delete(binaryContentId);
    }
}
