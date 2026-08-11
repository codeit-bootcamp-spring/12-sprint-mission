package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "role", source = "role")
    User toEntity(UserCreateRequest request, BinaryContent profile, String encodedPassword, Role role);

    @Mapping(target = "online", source = "online")
    UserResponse toResponse(User user, boolean online);

}