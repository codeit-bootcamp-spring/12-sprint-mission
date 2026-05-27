package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {

  default <T> PageResponse<T> fromSlice(Slice<T> slice, Instant nextCursor) {
    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext()
    );
  }

//  default <T> PageResponse<T> fromPage(Page<T> page, Object nextCursor) {
//    return new PageResponse<>(
//        page.getContent(),
//        nextCursor,
//        page.getSize(),
//        page.hasNext(),
//        page.getTotalElements()
//    );
//  }

}
