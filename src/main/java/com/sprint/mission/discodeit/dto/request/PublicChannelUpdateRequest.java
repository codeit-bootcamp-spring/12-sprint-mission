package com.sprint.mission.discodeit.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelUpdateRequest(
    String newName,
    String newDescription
) {

}
