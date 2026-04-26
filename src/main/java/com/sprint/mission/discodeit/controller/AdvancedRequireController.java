package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdvancedRequireController {
    private final BinaryContentService binaryContentService;
    private final UserService userService;


    @RequestMapping(path = "/user/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(path = "/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContent result = binaryContentService.find(binaryContentId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
