package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

    @Mapping(target = "online", source = "user", qualifiedByName = "resolveOnline")
    UserDto toDto(User user);

    @Named("resolveOnline")
    default Boolean resolveOnline(User user) {
        if (user == null || user.getUserStatus() == null) {
            return false;
        }
        return user.getUserStatus().isOnline();
    }

}
