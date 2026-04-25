package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class StaticApiController {

    private final UserService userService;
    private final BinaryContentService binaryContentService;

    public StaticApiController(
            UserService userService,
            BinaryContentService binaryContentService
    ) {
        this.userService = userService;
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(value = "/api/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findBinaryContent(
            @RequestParam UUID binaryContentId
    ) {
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }
}