package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private String profileImageType;
    private byte[] profileImage;
}
