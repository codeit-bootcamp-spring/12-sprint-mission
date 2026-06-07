package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  // Slice 기반 커서 페이지네이션 응답 생성
  // nextCursor: 마지막 항목에서 추출한 커서 값 (null이면 다음 페이지 없음)
  public <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }
}
