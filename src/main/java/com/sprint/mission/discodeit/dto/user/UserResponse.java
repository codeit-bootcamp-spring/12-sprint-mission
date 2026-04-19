package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.util.UUID;

@Getter
@Builder
@ToString
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private String nickname;

    private boolean online;
}