package com.sprint.mission.discodeit.dto.userstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusCreateRequest {
    private UUID userId;
}