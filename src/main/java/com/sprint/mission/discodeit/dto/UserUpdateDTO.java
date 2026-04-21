package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserUpdateDTO {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String profileImageType;
    private byte[] profileImage;
}
