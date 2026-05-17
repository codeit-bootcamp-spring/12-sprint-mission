package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public <T> PageResponse<T> fromCursor(
      List<T> content,
      Object nextCursor,
      int size,
      boolean hasNext,
      Long totalElements
  ) {
    return new PageResponse<>(
        content,
        nextCursor,
        size,
        hasNext,
        totalElements
    );
  }
}
