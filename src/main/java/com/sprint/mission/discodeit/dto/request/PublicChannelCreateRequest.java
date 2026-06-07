package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 500) String description
) {}
