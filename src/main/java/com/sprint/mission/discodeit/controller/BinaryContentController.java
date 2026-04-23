package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // [ ] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> find(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) List<UUID> ids
    ) {
        if (ids != null && !ids.isEmpty()) {
            List<BinaryContent> list = binaryContentService.findAllByIdIn(ids);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }

        if(id != null) {
            BinaryContent result = binaryContentService.find(id);
            return ResponseEntity.status(HttpStatus.OK).body(List.of(result));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
