package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

  public <E, D> PageResponse<D> fromPage(Page<E> page, List<D> convertedContent) {
    return new PageResponse<>(
        convertedContent,
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }

  public <E, D> PageResponse<D> fromSlice(Slice<E> slice, List<D> convertedContent) {
    return new PageResponse<>(
        convertedContent,
        slice.getNumber(),
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }
}