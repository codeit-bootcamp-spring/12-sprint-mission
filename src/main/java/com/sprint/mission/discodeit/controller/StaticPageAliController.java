package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StaticPageAliController {
    private final UserService userService;
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/api/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findBinaryContent(@RequestParam UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }
}
